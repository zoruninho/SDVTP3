package mediatheque;
/**
 * Une exception <TT>OperationImpossible</TT> est levee
 * en cas d'erreur. Par exemple,
 * <UL>
 * <LI>client deja inscrit
 * <LI>nombre maximal de documents atteint pour un client</LI>
 * <LI>document(s) non restitue(s) dans les delais</LI>
 * <LI>document inexistant</LI>
 * <LI>document non empruntable</LI>
 * <LI>...</LI>
 * </UL>
 */
public class OperationImpossible extends Exception {

  /** serial number.	 */
	private static final long serialVersionUID = 1L;

/**
   * Constructeur de l'exception OperationImpossible.
   *   @param message Message d'erreur
   */
  public OperationImpossible(final String message) {
    super(message);
  }
}

