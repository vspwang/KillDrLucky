package killdrlucky;

/**.
 * Represents the possible outcomes of an attack attempt
 * on Dr Lucky by a player.
 */
public enum AttackStatus {
  /** Attack succeeded. */
  SUCCESS,

  /** Player and target are not in the same space. */
  NOT_SAME_SPACE,

  /** Attack was witnessed by other players in visible spaces. */
  SEEN_BY_OTHERS,

  /** Player does not have the specified item. */
  NO_SUCH_ITEM,

  /** Target is already dead and cannot be attacked again. */
  TARGET_ALREADY_DEAD
}
