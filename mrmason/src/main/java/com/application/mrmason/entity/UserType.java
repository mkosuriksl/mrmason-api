package com.application.mrmason.entity;

public enum UserType {
    Developer,
    EC,
    Adm,
    worker,
    MS,
	RT;
	
    public static UserType fromString(String userType) {
        switch (userType.toLowerCase()) {
            case "worker":
                return worker;
            case "developer":
                return Developer;
            case "adm":
                return Adm;
            case "ec":
            	return EC;
            case "ms":
            	return MS;
            case "rt":
            	return RT;
            default:
                throw new IllegalArgumentException("Unknown user type: " + userType);
        }
    }
}
