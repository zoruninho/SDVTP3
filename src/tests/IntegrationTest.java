package tests;

import mediatheque.FicheEmprunt;
import mediatheque.Genre;
import mediatheque.Localisation;
import mediatheque.OperationImpossible;
import mediatheque.client.CategorieClient;
import mediatheque.client.Client;
import mediatheque.document.Document;
import mediatheque.document.Video;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import util.InvariantBroken;

/**
 * Test Document
 * 
 * @author J Paul Gibson
 * @author C Bac
 */

public class IntegrationTest {
	Genre g;
	Localisation l;
	Document d1;
	CategorieClient cat;
	Client c1;

	@Before
	public void setUp() throws Exception {
		g = new Genre("Test_nom1");
		l = new Localisation("Test_salle1", "Test_rayon1");
		d1 = new Video("Test_code1", l, "Test_titre1", "Test_auteur1",
				"Test_annee1", g, 120, "Test_mentionLegale1");
		cat = new CategorieClient("TarifReduit", 4, 25.0, 1.0, 1.0, true);
		c1 = new Client("nom1", "prenom1", "adresse1", cat, 0);
	}

	@After
	public void tearDown() throws Exception {
		l = null;
		g = null;
		d1 = null;
		cat = null;
		c1 = null;
	}

	@Test
	public void creationFicheEmpruntDocEmpruntable()
			throws OperationImpossible, InvariantBroken {
		// INTEGRATION TEST 1 - Emprunter - Verify correct co-ordination by
		// FicheEmprunt
		d1.metEmpruntable();
		FicheEmprunt f1 = new FicheEmprunt(c1, d1);
		// check that the client, document and genre states have been updated correctly
		Assert.assertEquals(1, d1.getNbEmprunts());
		Assert.assertEquals(1, c1.getNbEmpruntsEffectues());
		Assert.assertEquals(1, g.getNbEmprunts());
		// check that the duration and paiement information are ok
		Assert.assertTrue(f1.getDureeEmprunt() == 14);
		Assert.assertTrue( f1.getTarifEmprunt() == 1.5);
	}

	@Test(expected = OperationImpossible.class)
	public void creationFicheEmpruntDocNonEmpruntable()
			throws OperationImpossible, InvariantBroken {
		// INTEGRATION TEST 2 - Emprunter - co-ordination when Document not
		// empruntable
		new FicheEmprunt(c1, d1);
	}

}

