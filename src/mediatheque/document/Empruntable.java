package mediatheque.document;
/**
 * L'interface <TT>Empruntable</TT> donne les operations que
 * tout type de document doit fournir pour pouvoir etre emprunte.
 */
public interface Empruntable {

    /**
     * <TT>dureeEmprunt</TT> retourne la duree nominale de l emprunt
     * du document en nombre de jours.
     *    @return Duree de pret
     */
    int dureeEmprunt();

    /**
     * <TT>tarifEmprunt</TT> retourne le tarif nominal de l emprunt
     * du document.
     *    @return Tarif emprunt
     */
    double tarifEmprunt();
}
