package com.example.fatecCarCarona.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.fatecCarCarona.dto.OpenstreetmapDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OpenstreetmapService {
	 private final String baseUrl = "https://nominatim.openstreetmap.org/search?q=";
	    public Optional<OpenstreetmapDTO> buscarLocal(String local) {
	        try {
	            String localEncoded = URLEncoder.encode(local, StandardCharsets.UTF_8);
	            String urlString = baseUrl + localEncoded + "&format=json&addressdetails=1"; 
	            URL url = new URL(urlString);

	            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
	            conexao.setRequestMethod("GET");
	            conexao.setRequestProperty("User-Agent", "fatec-carona-app");
	            if (conexao.getResponseCode() != 200) {
	                return Optional.empty();
	            }

	            BufferedReader br = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
	            StringBuilder retorno = new StringBuilder();
	            String line;

	            while ((line = br.readLine()) != null) {
	                retorno.append(line);
	            }

	            ObjectMapper objectMapper = new ObjectMapper();
	            OpenstreetmapDTO[] resultados = objectMapper.readValue(retorno.toString(), OpenstreetmapDTO[].class);

	            if (resultados.length > 0) {
	                return Optional.of(resultados[0]); // retorna o primeiro resultado
	            } else {
	                 throw new Exception("Endereco não encontrado");
	            }

	        } catch (Exception e) {
	        	throw new RuntimeException("Erro ao buscar endereço: " + e.getMessage(), e);
	        }
	    }
}
