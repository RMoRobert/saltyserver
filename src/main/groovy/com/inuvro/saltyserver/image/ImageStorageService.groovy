package com.inuvro.saltyserver.image

import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
class ImageStorageService {
    
    private static final Logger log = LoggerFactory.getLogger(ImageStorageService.class)
    
    @Value('${salty.recipelibrary.images-storage-path:/data/images}')
    private String storagePath
    
    private Path rootLocation
    
    @PostConstruct
    void init() {
        // Expand ~ to user home directory (Spring config doesn't do this automatically, sort of a workaround)
        String expandedPath = storagePath
        if (expandedPath.startsWith("~")) {
            expandedPath = System.getProperty("user.home") + expandedPath.substring(1)
        }
        
        rootLocation = Paths.get(expandedPath).toAbsolutePath().normalize()
        log.info("Image storage initialized at: {}", rootLocation)
        try {
            Files.createDirectories(rootLocation)
        } catch (IOException e) {
            throw new RuntimeException("Could not create image storage directory: ${rootLocation}", e)
        }
    }
    
    /**
     * Store a file with a specific filename.
     * @param file The uploaded file
     * @param filename The filename to use (typically recipe's imageFilename)
     * @return The stored filename
     */
    String store(MultipartFile file, String filename) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file")
        }
        
        // Sanitize filename to prevent directory traversal
        String sanitizedFilename = sanitizeFilename(filename)
        
        try {
            Path destinationFile = rootLocation.resolve(sanitizedFilename).normalize()
            
            // Security check: ensure file is within storage directory
            if (!destinationFile.startsWith(rootLocation)) {
                throw new SecurityException("Cannot store file outside of storage directory")
            }
            
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING)
            return sanitizedFilename
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: ${sanitizedFilename}", e)
        }
    }
    
    /**
     * Store a file and generate a unique filename based on recipe ID.
     * Cleans up any existing images for this recipe ID (any extension) first.
     * @param file The uploaded file
     * @param recipeId The recipe ID to use as base for filename
     * @return The generated filename
     */
    String storeForRecipe(MultipartFile file, String recipeId) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file")
        }
        
        // Clean up any existing images for this recipe (handles format changes)
        deleteAllForRecipe(recipeId)
        
        String originalFilename = file.getOriginalFilename() ?: "image"
        String extension = getFileExtension(originalFilename)
        String filename = "${recipeId}${extension}"
        
        return store(file, filename)
    }
    
    /**
     * Delete all image files for a recipe ID (any extension).
     * This handles cleanup when image format changes (e.g., jpg -> png).
     * @param recipeId The recipe ID
     */
    void deleteAllForRecipe(String recipeId) {
        if (recipeId == null || recipeId.isEmpty()) {
            return
        }
        
        String sanitizedId = sanitizeFilename(recipeId)
        def extensions = ['.jpg', '.jpeg', '.png', '.gif', '.webp', '.heic']
        
        for (String ext : extensions) {
            try {
                Path file = rootLocation.resolve("${sanitizedId}${ext}").normalize()
                if (file.startsWith(rootLocation) && Files.exists(file)) {
                    Files.delete(file)
                    log.info("Deleted old image: ${sanitizedId}${ext}")
                }
            } catch (IOException e) {
                log.warn("Could not delete ${sanitizedId}${ext}: ${e.message}")
            }
        }
    }
    
    /**
     * Load a file as a Resource.
     * @param filename The filename to load
     * @return The file as a Resource, or null if not found
     */
    Resource load(String filename) {
        try {
            String sanitizedFilename = sanitizeFilename(filename)
            if (sanitizedFilename != filename) {
                log.debug("Filename sanitized: '{}' -> '{}'", filename, sanitizedFilename)
            }
            
            Path file = rootLocation.resolve(sanitizedFilename).normalize()
            log.debug("Looking for image at: {}", file)
            
            // Security check
            if (!file.startsWith(rootLocation)) {
                log.warn("Security check failed - path outside storage: {}", file)
                return null
            }
            
            Resource resource = new UrlResource(file.toUri())
            if (resource.exists() && resource.isReadable()) {
                long fileSize = Files.size(file)
                String detectedFormat = detectImageFormat(file)
                log.debug("Found image: {} ({} bytes, format: {})", file, fileSize, detectedFormat)
                
                // Warn if format may not display in browsers
                if (detectedFormat in ['HEIC', 'HEIF', 'UNKNOWN']) {
                    log.warn("Image {} has format '{}' which may not display in browsers", filename, detectedFormat)
                }
                
                return resource
            }
            
            // List files in directory to help debug
            log.warn("Image file not found: {}", file)
            log.debug("Files in storage directory: {}", 
                Files.list(rootLocation).map { it.fileName.toString() }.toList().take(20))
            
            return null
        } catch (Exception e) {
            log.error("Error loading image '{}': {}", filename, e.message)
            return null
        }
    }
    
    /**
     * Detect image format by reading magic bytes (file signature).
     */
    private String detectImageFormat(Path file) {
        try {
            byte[] header = new byte[12]
            Files.newInputStream(file).withCloseable { input ->
                input.read(header)
            }
            
            // JPEG: FF D8 FF
            if (header[0] == (byte)0xFF && header[1] == (byte)0xD8 && header[2] == (byte)0xFF) {
                return "JPEG"
            }
            
            // PNG: 89 50 4E 47 0D 0A 1A 0A
            if (header[0] == (byte)0x89 && header[1] == (byte)0x50 && header[2] == (byte)0x4E && header[3] == (byte)0x47) {
                return "PNG"
            }
            
            // GIF: 47 49 46 38
            if (header[0] == (byte)0x47 && header[1] == (byte)0x49 && header[2] == (byte)0x46 && header[3] == (byte)0x38) {
                return "GIF"
            }
            
            // WebP: 52 49 46 46 ... 57 45 42 50
            if (header[0] == (byte)0x52 && header[1] == (byte)0x49 && header[2] == (byte)0x46 && header[3] == (byte)0x46 &&
                header[8] == (byte)0x57 && header[9] == (byte)0x45 && header[10] == (byte)0x42 && header[11] == (byte)0x50) {
                return "WEBP"
            }
            
            // HEIC/HEIF: Check for 'ftyp' box with heic/mif1/heif brand
            // Typically starts at offset 4: 66 74 79 70 (ftyp)
            if (header[4] == (byte)0x66 && header[5] == (byte)0x74 && header[6] == (byte)0x79 && header[7] == (byte)0x70) {
                String brand = new String(header, 8, 4)
                if (brand in ['heic', 'heix', 'hevc', 'mif1', 'msf1']) {
                    return "HEIC"
                }
                if (brand in ['avif', 'avis']) {
                    return "AVIF"
                }
            }
            
            // Log hex for debugging unknown formats
            String hexHeader = header.collect { String.format("%02X", it) }.join(" ")
            log.debug("Unknown image format, header bytes: {}", hexHeader)
            return "UNKNOWN"
            
        } catch (Exception e) {
            log.warn("Could not detect image format for {}: {}", file, e.message)
            return "ERROR"
        }
    }
    
    /**
     * Delete a file.
     * @param filename The filename to delete
     * @return true if deleted, false otherwise
     */
    boolean delete(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false
        }
        
        try {
            String sanitizedFilename = sanitizeFilename(filename)
            Path file = rootLocation.resolve(sanitizedFilename).normalize()
            
            // Security check
            if (!file.startsWith(rootLocation)) {
                return false
            }
            
            return Files.deleteIfExists(file)
        } catch (IOException e) {
            return false
        }
    }
    
    /**
     * Check if a file exists.
     * @param filename The filename to check
     * @return true if exists, false otherwise
     */
    boolean exists(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false
        }
        
        try {
            String sanitizedFilename = sanitizeFilename(filename)
            Path file = rootLocation.resolve(sanitizedFilename).normalize()
            
            if (!file.startsWith(rootLocation)) {
                return false
            }
            
            return Files.exists(file)
        } catch (Exception e) {
            return false
        }
    }
    
    /**
     * Get the content type for a file based on extension.
     */
    String getContentType(String filename) {
        String extension = getFileExtension(filename).toLowerCase()
        switch (extension) {
            case '.jpg':
            case '.jpeg':
                return 'image/jpeg'
            case '.png':
                return 'image/png'
            case '.gif':
                return 'image/gif'
            case '.webp':
                return 'image/webp'
            case '.heic':
                return 'image/heic'
            default:
                return 'application/octet-stream'
        }
    }
    
    /**
     * Get the storage path for external reference.
     */
    Path getStoragePath() {
        return rootLocation
    }
    
    private String sanitizeFilename(String filename) {
        // Remove any path components, keeping only the filename
        String name = Paths.get(filename).getFileName().toString()
        // Remove any potentially dangerous characters
        return name.replaceAll('[^a-zA-Z0-9._-]', '_')
    }
    
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.')
        if (lastDot > 0) {
            return filename.substring(lastDot)
        }
        return '.jpg' // default extension
    }
}
