/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2013 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/


package atg.projects.store.actor;

import atg.nucleus.GenericService;
import atg.service.actor.Actor;
import atg.service.actor.ActorContext;
import atg.service.actor.ActorContextFactory;
import atg.service.actor.ActorException;
import atg.service.actor.ActorUtils;
import atg.service.actor.ModelMap;
import atg.service.actor.ModelMapFactory;
import atg.service.actor.VariantActor;

import com.endeca.infront.assembler.ContentItem;

/**
 * Invokes an {@link Actor} and makes a ContentItem available to the Actor with
 * the contentItemPropertyName key.
 * 
 * @author cbarthle
 * @version $Change: 794592 $$DateTime: 2013/03/04 17:50:17 $$Author: cbarthle $
 * @updated $DateTime: 2013/03/04 17:50:17 $$Author: cbarthle $
 */

public class ActorExecutor extends GenericService {
  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Change: 794592 $$DateTime: 2013/03/04 17:50:17 $$Author: cbarthle $";

  //-------------------------------------
  // Constants
  //-------------------------------------

  //-------------------------------------
  // Member variables
  //-------------------------------------

  //-------------------------------------
  // Properties
  //-------------------------------------
  private ModelMapFactory mModelMapFactory;

  /** @see atg.service.actor.ModelMapFactory */
  public ModelMapFactory getModelMapFactory() {
    return mModelMapFactory;
  }

  /** @see atg.service.actor.ModelMapFactory */
  public void setModelMapFactory(ModelMapFactory pModelMapFactory) {
    mModelMapFactory = pModelMapFactory;
  }

  private ActorContextFactory mActorContextFactory;

  /** @see atg.service.actor.ActorContextFactory */
  public ActorContextFactory getActorContextFactory() {
    return mActorContextFactory;
  }

  /** @see atg.service.actor.ActorContextFactory */
  public void setActorContextFactory(ActorContextFactory pActorContextFactory) {
    mActorContextFactory = pActorContextFactory;
  }

  private String mContentItemPropertyName = "currentContentItem";

  /**
   * The key of the content item property available to the actor.
   * 
   * @return the contentItemPropertyName
   */
  public String getContentItemPropertyName() {
    return mContentItemPropertyName;
  }

  /**
   * Sets the key for the content item property.
   * 
   * @param pContentItemPropertyName the contentItemPropertyName to set
   */
  public void setContentItemPropertyName(String pContentItemPropertyName) {
    mContentItemPropertyName = pContentItemPropertyName;
  }

  private String mOutputModelPropertyName = "atg:contents";

  /**
   * The property name in ContentItem of the actor output
   * 
   * @return the outputModelPropertyName
   */
  public String getOutputModelPropertyName() {
    return mOutputModelPropertyName;
  }

  /**
   * Sets the property name in ContentItem of the actor output
   * 
   * @param pOutputModelPropertyName the outputModelPropertyName to set
   */
  public void setOutputModelPropertyName(String pOutputModelPropertyName) {
    mOutputModelPropertyName = pOutputModelPropertyName;
  }

  //-------------------------------------
  // Constructors
  // -------------------------------------

  //-------------------------------------
  // Public Methods
  //-------------------------------------
  /**
   * Invokes the given actor. The ContentItem is made avaialble in the actor
   * context. The Actor output model map is then added to the ContentItem.
   * 
   * @param pActor The actor to invoke
   * @param pContentItem The ContentItem to update
   * @return The updated content item
   */
  public ContentItem invokeActor(Actor pActor, ContentItem pContentItem) {
    if (pActor == null) {
      vlogError("Actor is null");
      return pContentItem;
    }
    try {
      // Invoke Actor
      ActorContext actorContext = getActorContextFactory().createActorContext();
      // Set the content item so the actor can access it
      actorContext.putAttribute(getContentItemPropertyName(), pContentItem);

      ModelMap modelMap = getModelMapFactory().createModelMap();
      // TODO support specifying chain id
      String chainId = null;

      if (chainId == null && pActor instanceof VariantActor) {
        chainId = ((VariantActor) pActor).getDefaultActorChainId();
        // set the chainId in the context..
        ActorUtils.putActorChainId(actorContext, chainId);
      }
      pActor.act(actorContext, modelMap);
      pContentItem.put(getOutputModelPropertyName(), modelMap);
    }
    catch (ActorException e) {
      vlogError("Error executing actor");
    }
    return pContentItem;
  }
  //-------------------------------------
  // Private Methods
  //-------------------------------------

}
