/**
 * CArtAgO - DEIS, University of Bologna
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package cartago.events;

import cartago.ArtifactId;
import cartago.ArtifactObsProperty;
import cartago.CartagoEvent;
import cartago.OpId;
import cartago.Tuple;

/**
 * Class representing an observable event generated by an artifact
 * 
 * @author aricci
 *
 */
public class ArtifactObsEvent extends CartagoEvent {
	
	private ArtifactId src;
	private Tuple signal;
	private ArtifactObsProperty[] propsChanged;
	private ArtifactObsProperty[] propsAdded;
	private ArtifactObsProperty[] propsRemoved;
	
	
	public ArtifactObsEvent() {  }
	
	public ArtifactObsEvent(long id, ArtifactId src, Tuple signal, ArtifactObsProperty[] propsChanged, ArtifactObsProperty[] propsAdded, ArtifactObsProperty[] propsRemoved){
		super(id);
		this.src = src;
		this.signal = signal;
		this.propsChanged = propsChanged;
		this.propsAdded = propsAdded;
		this.propsRemoved = propsRemoved;
	}

	/**
	 * Get the possible signal, source of the event
	 * 
	 * @return
	 */
	public Tuple getSignal(){
		return signal;
	}

	/**
	 * Get the changed properties
	 * 
	 * @return
	 */
	public ArtifactObsProperty[] getChangedProperties(){
		return propsChanged;
	}
	
	/**
	 * Get the added properties
	 * 
	 * @return
	 */
	public ArtifactObsProperty[] getAddedProperties(){
		return propsAdded;
	}

	/**
	 * Get the removed properties
	 * 
	 * @return
	 */
	public ArtifactObsProperty[] getRemovedProperties(){
		return propsRemoved;
	}

	/**
	 * Get event source id
	 * 
	 * @return source id
	 */
	public ArtifactId getArtifactId(){
		return src;
	}
	
	public String toString(){
		return "obs-event-"+getId()+"-"+src;
		//return signal.toString();
	}

}
