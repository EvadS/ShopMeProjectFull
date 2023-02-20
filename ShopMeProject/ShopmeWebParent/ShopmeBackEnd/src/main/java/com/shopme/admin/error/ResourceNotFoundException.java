package com.shopme.admin.error;

public class ResourceNotFoundException extends RuntimeException {

    private String resource;
    private String searchField;
    private String value;



    public ResourceNotFoundException(String resource, String searchField, String value) {
        super();
        this.resource = resource;
        this.searchField = searchField;
        this.value = value;
    }

    //TODO: deprecated. Use in tutorial
    public  ResourceNotFoundException(String message){
        super(message);
    }

}
