/**
 * 
 */
package sim.graphics.material;

import sim.exception.SRuntimeException;

/**
 * L'interface STextureMaterial correspond à un matériel contenant des textures.
 * @author Simon Vézina
 * @since 2015-10-31
 * @version 2015-12-08
 */
public interface STextureMaterial extends SMaterial {

  /**
   * Méthode permettant de modifier le format d'interprétation des coordonnées uv de texture.
   * @param uv_format - Le code d'interprétation des coordonnée uv.
   * @throws SRuntimeException Si le code d'interprétation des coordonnées uv n'est pas reconnu par le système.
   * @throws SRuntimeException Si la définition a déjà été réalisé une fois. Une texture ne peut pas se faire modifier son code d'interprétation plus d'une fois.
   */
  public void setUVFormat(int uv_format) throws SRuntimeException;
  
  /**
   * Méthode déterminant si le code d'interprétation des coordonnées uv de texture a été défini pour cette texture.
   * @return <b>true</b> si le code a été défini et <b>false</b> sinon.
   */
  public boolean isUVFormatSelected();
  
}//fin de l'interface STextureMaterial
