
dependency-tree:
	mvn dependency:tree

dependency-purge:
	mvn dependency:purge-local-repository


swagger-ui:
	docker run --rm -p 8081:8080 -e SWAGGER_JSON_URL=http://localhost:8080/api/openapi.json swaggerapi/swagger-ui