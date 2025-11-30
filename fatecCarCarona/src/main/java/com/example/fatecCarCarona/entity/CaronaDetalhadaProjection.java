package com.example.fatecCarCarona.entity;

public interface CaronaDetalhadaProjection {
    String getNome();
    String getSobrenome();
    String getFoto();
    String getTelefone();
    java.sql.Timestamp getDataHora(); // ou LocalDateTime se quiser testar
    Integer getVagas();
    String getStatusNome();
    String getModelo();
    String getMarca();
    String getPlaca();
    String getCor();
    Integer getAno();
    Long getIdOrigem();
    String getOrigemLogradouro();
    String getOrigemNumero();
    String getOrigemBairro();
    String getOrigemCep();
    Double getOrigemLatitude();
    Double getOrigemLongitude();
    Long getIdDestino();
    String getDestinoLogradouro();
    String getDestinoNumero();
    String getDestinoBairro();
    String getDestinoCep();
    Double getDestinoLatitude();
    Double getDestinoLongitude();
}