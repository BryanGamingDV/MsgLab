package me.bryangaming.chatlab.debug;

public enum LoggerTypeEnum {

    WARNING("WARNING"), ERROR("ERROR"), SUCCESSFULL("SUCESSFULL");

    private final String typeName;

    LoggerTypeEnum(String typeName){
        this.typeName = typeName;
    }

    public String getName() {
        return typeName;
    }
}
