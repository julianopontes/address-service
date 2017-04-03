package com.jop.address;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { Application.class })
@WebAppConfiguration
@IntegrationTest("server.port=10001")
public abstract class ApplicationTest {

	private static List<Object> toPersist = new ArrayList<Object>();

	private final String server;

	@Autowired
	private JpaTransactionManager manager;

	@Autowired
	private JdbcTemplate template;

	public ApplicationTest() {
		this.server = "http://localhost:10001";
	}

	@Before
	public void setUp() {
		toPersist.clear();
		template.execute("TRUNCATE SCHEMA public AND COMMIT");
	}

	protected void add(Object... objects) {
		for (Object object : objects) {
			toPersist.add(object);
		}
	}

	protected void saveall(Object... objects) {
		this.add(objects);
		this.saveall();
	}

	protected void saveall() {
		EntityManager em = manager.getEntityManagerFactory().createEntityManager();
		em.getTransaction().begin();
		for (Object object : toPersist) {
			em.persist(object);
		}
		em.flush();
		em.clear();
		em.close();
		toPersist.clear();
		em.getTransaction().commit();
	}

	protected RequestBuilder get(String uri) {
		return new RequestBuilder(server, uri, HttpMethod.GET);
	}

	protected RequestBuilder post(String uri) {
		return new RequestBuilder(server, uri, HttpMethod.POST);
	}

	protected RequestBuilder put(String uri) {
		return new RequestBuilder(server, uri, HttpMethod.PUT);
	}

	protected RequestBuilder delete(String uri) {
		return new RequestBuilder(server, uri, HttpMethod.DELETE);
	}

	// With path variables
	protected RequestBuilder get(String uri, Object... path) {
		return get(String.format(uri, path));
	}

	protected RequestBuilder post(String uri, Object... path) {
		return post(String.format(uri, path));
	}

	protected RequestBuilder put(String uri, Object... path) {
		return put(String.format(uri, path));
	}

	protected RequestBuilder delete(String uri, Object... path) {
		return delete(String.format(uri, path));
	}
}