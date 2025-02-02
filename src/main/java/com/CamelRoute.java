package com.example;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

public class CamelRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:start")
            .unmarshal().json(JsonLibrary.Jackson) // Deserializa el JSON a un objeto Java
            .process(exchange -> {
                // Obtener el objeto original
                Map<String, Object> originalJson = exchange.getIn().getBody(Map.class);
                
                // Crear el nuevo JSON según la estructura solicitada
                Map<String, Object> newJson = new HashMap<>();
                newJson.put("id", originalJson.get("id"));
                Map<String, Object> nameAndLocation = new HashMap<>();
                nameAndLocation.put("name", originalJson.get("name"));
                nameAndLocation.put("lat", ((Map<String, Double>) originalJson.get("location")).get("lat"));
                nameAndLocation.put("lng", ((Map<String, Double>) originalJson.get("location")).get("lng"));
                newJson.put("nameAndLocation", nameAndLocation);
                newJson.put("type", originalJson.get("type"));
                newJson.put("status", originalJson.get("status"));

                // Establecer el nuevo JSON como el cuerpo del mensaje
                exchange.getIn().setBody(newJson);
            })
            .marshal().json(JsonLibrary.Jackson) // Vuelve a serializar el nuevo objeto a JSON
            .to("log:com.example.camelquarkusjsontransform?level=INFO");
    }
}
