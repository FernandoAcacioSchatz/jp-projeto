package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Excluir auto-configurações relacionadas a DataSource/Hibernate/JPA nos
// testes de contexto para evitar que o Spring tente inicializar um
// DataSource (não usamos H2 em testes). Isso impede erros de driver ausente.
@SpringBootTest(properties = {
	"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration"
})
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}

}
