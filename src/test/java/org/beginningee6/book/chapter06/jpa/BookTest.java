package org.beginningee6.book.chapter06.jpa;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BookTest {
	
	private static final Logger logger = Logger.getLogger(BookTest.class.getName());
	
	@Deployment
	public static Archive<?> createDeployment() {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
			.addPackage(Book.class.getPackage())
			.addAsManifestResource("test-persistence.xml", "persistence.xml")
			.addAsManifestResource("jbossas-ds.xml")
			.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

		logger.info(archive.toString(true));

		return archive;
	}
	
	@PersistenceContext
	EntityManager em;
	
	@Inject
	UserTransaction userTransaction;
	
	@Before
	public void setUp() throws Exception {
		clearData();
	}
	
	private void clearData() throws Exception {
		userTransaction.begin();
		em.joinTransaction();

		logger.info("Dumping old records...");
		
		em.createQuery("DELETE FROM Book").executeUpdate();
		userTransaction.commit();
	}
	
	/**
	 * 
	 * Bookエンティティの永続化のテスト。
	 * 
	 */
	@Test
	public void testCreateABook() throws Exception {
		
		///// 準備 /////
		
		Book book = new Book();
        book.setTitle("The Hitchhiker's Guide to the Galaxy");
        book.setPrice(12.5F);
        book.setDescription("Science fiction comedy book");
        book.setIsbn("1-84023-742-2");
        book.setNbOfPage(354);
        book.setIllustrations(false);

        ///// テスト /////
        
        userTransaction.begin();
        em.joinTransaction();
        
        em.persist(book);
        
        userTransaction.commit();
        
        ///// 検証 /////
        
        assertThat(book.getId(), is(notNullValue()));        
	}
	
	/**
	 * 
	 * Bookエンティティで定義した名前付きクエリのテスト。
	 * 
	 */
	@Test
	public void testFindOneBook() throws Exception {
		
		///// 準備 /////
		
		Book book = new Book();
        book.setTitle("The Hitchhiker's Guide to the Galaxy");
        book.setPrice(12.5F);
        book.setDescription("Science fiction comedy book");
        book.setIsbn("1-84023-742-2");
        book.setNbOfPage(354);
        book.setIllustrations(false);

        userTransaction.begin();
        em.joinTransaction();
        
        em.persist(book);
        
        userTransaction.commit();

        ///// テスト /////
        
        List<Book> books = em.createNamedQuery("findAllBooks", Book.class).getResultList();
        
        ///// 検証 /////
        
        assertThat(books.size(), is(1));
        assertThat(books.get(0).getTitle(), is("The Hitchhiker's Guide to the Galaxy"));
	}

}
