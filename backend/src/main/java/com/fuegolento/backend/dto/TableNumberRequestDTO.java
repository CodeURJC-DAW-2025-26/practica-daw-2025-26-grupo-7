package com.fuegolento.backend.dto;

public class TableNumberRequestDTO {

    private Integer tableNumber;

    public TableNumberRequestDTO() {
    }

    public TableNumberRequestDTO(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }
}