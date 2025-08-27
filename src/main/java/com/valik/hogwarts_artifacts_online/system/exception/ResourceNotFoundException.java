package com.valik.hogwarts_artifacts_online.system.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String objectName, String id) {
        super("Could not find " + objectName + " with Id " + id);
    }

    public ResourceNotFoundException(String objectName, Integer id) {
        super("Could not find " + objectName + " with Id " + id);
    }

}
