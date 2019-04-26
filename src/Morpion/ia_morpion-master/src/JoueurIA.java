

public abstract class JoueurIA extends Joueur {

	protected Action actionMemorisee;
	
	
	public JoueurIA(int id, String nom) {
		super(id, nom);
	}

	public void setActionMemorisee(Action actionMemorisee) {
		this.actionMemorisee = actionMemorisee;
	}
	
	public Action getActionMemorisee() {
		return actionMemorisee;
		
	}
	
	public final void memoriserAction(Action action) {
		
	       if (action != null) {
	            System.out.println("##############################");
	            System.out.println("Action memorisee :" + actionMemorisee.toString());
	            System.out.println("##############################");
	        }
	        else {
	        	this.actionMemorisee = action;
	        }
		
	}
	
}
