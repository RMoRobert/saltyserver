package com.inuvro.saltyserver.image

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/recipes/images")
class ImageController {
    
    private static final Logger log = LoggerFactory.getLogger(ImageController.class)
    
    private final ImageStorageService imageStorageService
    
    ImageController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService
    }
    
    /**
     * Download/serve an image by filename.
     * GET /api/recipes/images/{filename}
     */
    @GetMapping("/{filename:.+}")
    ResponseEntity<Resource> getImage(@PathVariable("filename") String filename) {
        log.debug("Image request for filename: {}", filename)
        
        Resource resource = imageStorageService.load(filename)
        
        if (resource == null) {
            log.warn("Image not found: {}", filename)
            return ResponseEntity.notFound().build()
        }
        
        String contentType = imageStorageService.getContentType(filename)
        log.debug("Serving image {} with content-type: {}", filename, contentType)
        
        // Check if HEIC - browsers don't support it natively
        if (contentType == 'image/heic') {
            log.warn("HEIC image requested: {} - browsers may not display this format", filename)
        }
        
        // Use shorter cache for development, or add version query param for cache-busting
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CACHE_CONTROL, "max-age=300") // Cache for 5 minutes
                .header(HttpHeaders.ETAG, "\"" + resource.contentLength() + "\"") // ETag for cache validation
                .body(resource)
    }
    
    /**
     * Upload a new image with a specific filename.
     * POST /api/recipes/images?filename=myimage.jpg
     */
    @PostMapping
    ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "filename", required = false) String filename) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body([error: "No file provided"])
        }
        
        // Validate file type
        String contentType = file.getContentType()
        if (!isValidImageType(contentType)) {
            return ResponseEntity.badRequest()
                    .body([error: "Invalid file type. Allowed: JPEG, PNG, GIF, WebP"])
        }
        
        try {
            String storedFilename
            if (filename != null && !filename.isEmpty()) {
                storedFilename = imageStorageService.store(file, filename)
            } else {
                // Generate a unique filename
                String originalName = file.getOriginalFilename() ?: "image.jpg"
                String uniqueName = UUID.randomUUID().toString() + getExtension(originalName)
                storedFilename = imageStorageService.store(file, uniqueName)
            }
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body([
                        filename: storedFilename,
                        url: "/api/recipes/images/${storedFilename}".toString()
                    ])
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body([error: "Failed to upload file: ${e.message}".toString()])
        }
    }
    
    /**
     * Delete an image by filename.
     * DELETE /api/recipes/images/{filename}
     */
    @DeleteMapping("/{filename:.+}")
    ResponseEntity<Void> deleteImage(@PathVariable("filename") String filename) {
        Boolean deleted = imageStorageService.delete(filename)
        
        if (deleted) {
            return ResponseEntity.noContent().build()
        } else {
            return ResponseEntity.notFound().build()
        }
    }
    
    /**
     * Check if an image exists.
     * HEAD /api/recipes/images/{filename}
     */
    @RequestMapping(value = "/{filename:.+}", method = RequestMethod.HEAD)
    ResponseEntity<Void> checkImage(@PathVariable("filename") String filename) {
        if (imageStorageService.exists(filename)) {
            return ResponseEntity.ok().build()
        } else {
            return ResponseEntity.notFound().build()
        }
    }
    
    private Boolean isValidImageType(String contentType) {
        if (contentType == null) return false
        return contentType.startsWith("image/")
    }
    
    private String getExtension(String filename) {
        int lastDot = filename.lastIndexOf('.')
        if (lastDot > 0) {
            return filename.substring(lastDot)
        }
        return ".jpg"
    }
}
