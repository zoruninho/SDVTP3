package tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mediatheque.Genre;
import mediatheque.Localisation;
import mediatheque.OperationImpossible;
import mediatheque.document.Document;
import mediatheque.document.Video;
import util.InvariantBroken;

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
		d1.restituer();
		d1.emprunter();
		d1.restituer();
		d1.emprunter();
		d1.restituer();
		d1.emprunter();
		d1.restituer();
		d1.emprunter();
		Assert.assertTrue(d1.invariant());
		Assert.assertEquals(5, d1.getNbEmprunts());
	}
	
	@Test(expected=OperationImpossible.class)
	public void testRestituerBeforeEmprunter() 
	throws OperationImpossible, InvariantBroken {
		Assert.assertTrue(d1.invariant());
		Assert.assertTrue(!d1.estEmprunte());
		d1.restituer();
	}
	
	@Test(expected=OperationImpossible.class)
	public void testDoubleEmprunt() 
	throws OperationImpossible, InvariantBroken {
		Assert.assertTrue(d1.invariant());
		d1.emprunter();
		d1.emprunter();
	}
	
	@Test(expected=OperationImpossible.class)
	public void testGenreNull()
	throws OperationImpossible, InvariantBroken {
		Document d2 = new Video("Test_code2", l, "Test_titre2", "Test_auteur2",
				"Test_annee2", null, 140, "Test_mentionLegale2");
	}

}