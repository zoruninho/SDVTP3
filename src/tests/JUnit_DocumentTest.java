package tests;

import mediatheque.Genre;
import mediatheque.Localisation;
import mediatheque.document.Document;
import mediatheque.document.Video;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Document Using JUnit
 * 
 * @author J Paul Gibson
 * @author C Bac
 * @author Denis Conan
 */

public class JUnit_DocumentTest {
	Localisation l;
	Genre g;
	Document d1;

	@Before
	public void setUp() throws Exception {
		g = new Genre("Test_nom1");
		l = new Localisation("Test_salle1", "Test_rayon1");

		d1 = new Video("Test_code1", l, "Test_titre1", "Test_auteur1",
				"Test_annee1", g, 120, "Test_mentionLegale1");
	}

	@After
	public void tearDown() throws Exception {
		l = null;
		g = null;
		d1 = null;
	}

	@Test
	// Test 1 du cours
	public void testDocumentConstructorInvariant() throws Exception {
		Assert.assertTrue(d1.invariant());
	}

	@Test
	// Test 2 du cours
	public void testReachableStates() throws Exception {
		d1.metEmpruntable();
		Assert.assertTrue(d1.invariant());
		d1.emprunter();
		Assert.assertTrue(d1.invariant());
	}
	
	@Test
	public void testCinqEmprunts() throws Exception {
		d1.metEmpruntable();
		Assert.assertTrue(d1.invariant());
		d1.emprunter();
		Assert.assertTrue(d1.invariant());
		d1.emprunter();
		d1.emprunter();
		d1.emprunter();
		d1.emprunter();
		Assert.assertEquals(5, d1.getNbEmprunts());
	}

}