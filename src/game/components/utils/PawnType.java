/**
 * 
 */
package game.components.utils;

/**
 * @author Bartek
 *
 */
public enum PawnType {
	one(1), two(-1);
	
	final int direction;
	
	PawnType(int direction){
		this.direction = direction;
	}
}
