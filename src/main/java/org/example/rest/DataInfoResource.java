package org.example.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.dto.ResultDTO;
import org.example.entities.ResultEntity;
import org.example.mappers.ResultMapper;
import org.example.repository.ResultRepository;
import org.example.service.CacheService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/data")
public class DataInfoResource {
    
    @Inject
    private CacheService cacheService;
    
    @Inject
    private ResultRepository resultRepository;
    
    @GET
    @Path("/cache")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCacheData() {
        try {
            Map<String, Object> response = new HashMap<>();
            
            if (!cacheService.isCacheAvailable()) {
                response.put("available", false);
                response.put("message", "Cache is not available");
                response.put("data", List.of());
                return Response.ok(response).build();
            }
            
            List<ResultDTO> cacheData = cacheService.getAll();
            response.put("available", true);
            response.put("size", cacheData.size());
            response.put("data", cacheData);
            
            return Response.ok(response).build();
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .build();
        }
    }
    

    @GET
    @Path("/database")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDatabaseData() {
        try {
            Map<String, Object> response = new HashMap<>();
            
            List<ResultEntity> entities = resultRepository.findAll();
            List<ResultDTO> dbData = entities.stream()
                    .map(ResultMapper::toDTO)
                    .collect(Collectors.toList());
            
            response.put("size", dbData.size());
            response.put("data", dbData);
            
            return Response.ok(response).build();
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .build();
        }
    }

    @GET
    @Path("/stats")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            

            boolean cacheAvailable = cacheService.isCacheAvailable();
            int cacheSize = 0;
            if (cacheAvailable) {
                cacheSize = cacheService.getAll().size();
            }
            
            Map<String, Object> cacheInfo = new HashMap<>();
            cacheInfo.put("available", cacheAvailable);
            cacheInfo.put("size", cacheSize);
            stats.put("cache", cacheInfo);
            

            List<ResultEntity> dbEntities = resultRepository.findAll();
            int dbSize = dbEntities.size();
            
            Map<String, Object> dbInfo = new HashMap<>();
            dbInfo.put("size", dbSize);
            stats.put("database", dbInfo);
            

            Map<String, Object> comparison = new HashMap<>();
            comparison.put("synchronized", cacheAvailable && cacheSize == dbSize);
            comparison.put("difference", Math.abs(cacheSize - dbSize));
            stats.put("comparison", comparison);
            
            return Response.ok(stats).build();
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .build();
        }
    }
    

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllData() {
        try {
            Map<String, Object> response = new HashMap<>();
            
            List<ResultDTO> cacheData = cacheService.isCacheAvailable() 
                    ? cacheService.getAll() 
                    : List.of();
            response.put("cache", Map.of(
                "available", cacheService.isCacheAvailable(),
                "size", cacheData.size(),
                "data", cacheData
            ));
            
            List<ResultEntity> dbEntities = resultRepository.findAll();
            List<ResultDTO> dbData = dbEntities.stream()
                    .map(ResultMapper::toDTO)
                    .collect(Collectors.toList());
            response.put("database", Map.of(
                "size", dbData.size(),
                "data", dbData
            ));
            
            return Response.ok(response).build();
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .build();
        }
    }
}

