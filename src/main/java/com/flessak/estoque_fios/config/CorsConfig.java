package com.flessak.estoque_fios.config;
// package = pacote/pasta
// config = pasta de configurações do projeto

import org.springframework.context.annotation.Configuration;
// importa @Configuration (marca classe de configuração)

import org.springframework.web.servlet.config.annotation.CorsRegistry;
// importa CorsRegistry (registro de configurações CORS)

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
// importa WebMvcConfigurer (interface pra configurar Spring MVC)

@Configuration
// @Configuration = marca essa classe como CONFIGURAÇÃO do Spring
// Spring vai carregar e aplicar essas configurações na inicialização
public class CorsConfig implements WebMvcConfigurer {
// public = visível pra todos
// class = declara classe
// CorsConfig = nome da classe
// implements = implementa (cumpre o contrato de)
// WebMvcConfigurer = interface do Spring MVC pra customizar configs

    @Override
    // @Override = sobrescreve método da interface
    public void addCorsMappings(CorsRegistry registry) {
        // public = acessível
        // void = não retorna nada
        // addCorsMappings = método que configura CORS
        // CorsRegistry registry = objeto que registra as regras CORS

        registry.addMapping("/**")
                // registry.addMapping(...) = adiciona mapeamento CORS
                // "/**" = aplica pra TODOS os endpoints
                //   - /** = qualquer caminho (/, /api/fios, /api/fios/5, etc)

                .allowedOriginPatterns("*")
                // .allowedOriginPatterns(...) = padrões de origem permitidos
                // "*" = QUALQUER origem (qualquer domínio/site)
                // ex: localhost:3000, localhost:5173, meusite.com, etc
                // ⚠️ OriginPatterns vs Origins:
                //   - allowedOrigins("*") NÃO funciona com allowCredentials(true)
                //   - allowedOriginPatterns("*") funciona com allowCredentials(true)

                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                // .allowedMethods(...) = métodos HTTP permitidos
                // "GET" = buscar/listar
                // "POST" = criar
                // "PUT" = atualizar completo
                // "DELETE" = deletar
                // "PATCH" = atualizar parcial
                // "OPTIONS" = preflight request (requisição de verificação CORS)

                .allowedHeaders("*")
                // .allowedHeaders(...) = cabeçalhos HTTP permitidos
                // "*" = qualquer cabeçalho
                // ex: Content-Type, Authorization, X-Custom-Header, etc

                .allowCredentials(true);
        // .allowCredentials(true) = permite envio de credenciais
        // credenciais = cookies, tokens de autenticação, HTTP auth
        // true = permite
        // false = não permite
    }
}
