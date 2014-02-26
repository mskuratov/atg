/*<ORACLECOPYRIGHT>
 * Copyright (C) 1994-2013 Oracle and/or its affiliates. All rights reserved.
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.
 * UNIX is a registered trademark of The Open Group.
 *
 * This software and related documentation are provided under a license agreement 
 * containing restrictions on use and disclosure and are protected by intellectual property laws. 
 * Except as expressly permitted in your license agreement or allowed by law, you may not use, copy, 
 * reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, 
 * or display any part, in any form, or by any means. Reverse engineering, disassembly, 
 * or decompilation of this software, unless required by law for interoperability, is prohibited.
 *
 * The information contained herein is subject to change without notice and is not warranted to be error-free. 
 * If you find any errors, please report them to us in writing.
 *
 * U.S. GOVERNMENT RIGHTS Programs, software, databases, and related documentation and technical data delivered to U.S. 
 * Government customers are "commercial computer software" or "commercial technical data" pursuant to the applicable 
 * Federal Acquisition Regulation and agency-specific supplemental regulations. 
 * As such, the use, duplication, disclosure, modification, and adaptation shall be subject to the restrictions and 
 * license terms set forth in the applicable Government contract, and, to the extent applicable by the terms of the 
 * Government contract, the additional rights set forth in FAR 52.227-19, Commercial Computer Software License 
 * (December 2007). Oracle America, Inc., 500 Oracle Parkway, Redwood City, CA 94065.
 *
 * This software or hardware is developed for general use in a variety of information management applications. 
 * It is not developed or intended for use in any inherently dangerous applications, including applications that 
 * may create a risk of personal injury. If you use this software or hardware in dangerous applications, 
 * then you shall be responsible to take all appropriate fail-safe, backup, redundancy, 
 * and other measures to ensure its safe use. Oracle Corporation and its affiliates disclaim any liability for any 
 * damages caused by use of this software or hardware in dangerous applications.
 *
 * This software or hardware and documentation may provide access to or information on content, 
 * products, and services from third parties. Oracle Corporation and its affiliates are not responsible for and 
 * expressly disclaim all warranties of any kind with respect to third-party content, products, and services. 
 * Oracle Corporation and its affiliates will not be responsible for any loss, costs, 
 * or damages incurred due to your access to or use of third-party content, products, or services.
 </ORACLECOPYRIGHT>*/

package atg.projects.store.mobile;

import atg.commerce.CommerceException;
import atg.commerce.gifts.GiftlistManager;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.multisite.SiteGroupManager;
import atg.projects.store.gifts.StoreGiftlistFormHandler;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

/**
 * This class extends CRS version of the GiftList form handler.
 * It implements additional methods required by the native CRS iOS client.
 * @author ATG
 * @version $Id: //hosting-blueprint/MobileCommerce/version/10.2/server/MobileCommerce/src/atg/projects/store/mobile/MobileStoreGiftlistFormHandler.java#3 $
 * @updated $DateTime: 2013/03/14 04:12:49 $$Author: abakinou $
 */
public class MobileStoreGiftlistFormHandler extends StoreGiftlistFormHandler
{
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/MobileCommerce/version/10.2/server/MobileCommerce/src/atg/projects/store/mobile/MobileStoreGiftlistFormHandler.java#3 $$Change: 796710 $";

  private String mSourceGiftlistId;
  private String mDestinationGiftlistId;
  private String mGiftItemId;
  private String mSkuId;
  private SiteGroupManager mSiteGroupManager;
  private String mScope;

  /**
   * This parameter is required by the <code>handleConvertWishListToGiftList</code>. Only gift items which are located
   * in this site scope will be moved to a newly created gift list during wish list conversion.
   * @return Site scope shareable ID.
   */
  public String getScope() {
    return mScope;
  }

  public void setScope(String pScope) {
    mScope = pScope;
  }

  /**
   * Link to the site group manager Nucleus component.
   * @return Site group manager to be used.
   */
  public SiteGroupManager getSiteGroupManager() {
    return mSiteGroupManager;
  }

  public void setSiteGroupManager(SiteGroupManager pSiteGroupManager) {
    mSiteGroupManager = pSiteGroupManager;
  }

  /**
   * This parameter is required by the <code>handleMoveItemToGiftlist</code> method. Giftlist item will be moved
   * from giftlist specified by this parameter. Set this parameter to <code>null</code> to move giftlist item
   * from user's wishlist.
   * @return Source giftlist ID to be used.
   */
  public String getSourceGiftlistId()
  {
    return mSourceGiftlistId;
  }

  public void setSourceGiftlistId(String pSourceGiftlistId)
  {
    mSourceGiftlistId = pSourceGiftlistId;
  }

  /**
   * This parameter is required by the <code>handleMoveItemToGiftlist</code> method. Giftlist item will be moved
   * to giftlist specified by this parameter. Set this parameter to <code>null</code> to move giftlist item
   * to user's wishlist.
   * @return Destination giftlist ID to be used.
   */
  public String getDestinationGiftlistId()
  {
    return mDestinationGiftlistId;
  }


  public void setDestinationGiftlistId(String pDestinationGiftlistId)
  {
    mDestinationGiftlistId = pDestinationGiftlistId;
  }

  /**
   * This parameter is required by the <code>handleMoveItemToGiftlist</code> method. Giftlist item specified
   * by this parameter will be moved.
   * @return Giftlist item ID to be used.
   */
  public String getGiftItemId()
  {
    return mGiftItemId;
  }

  public void setGiftItemId(String pGiftItemId)
  {
    mGiftItemId = pGiftItemId;
  }

  /**
   * This parameter is required by the <code>handleAddItemToWishList</code> method. SKU with this ID will be added.
   * @return SKU ID to be added to gift/wish list.
   */
  public String getSkuId()
  {
    return mSkuId;
  }

  public void setSkuId(String pSkuId)
  {
    mSkuId = pSkuId;
  }

  /**
   * This method creates a new gift list and then moves all gift items from user's wish list to the newly created gift list.
   * @param pRequest - Current HTTP request.
   * @param pResponse - Current HTTP response.
   * @return <code>true</code>, if request redirected and <code>false</code> otherwise.
   * @throws ServletException
   * @throws IOException
   * @throws CommerceException
   * @throws RepositoryException
   */
  public boolean handleConvertWishListToGiftList(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
          throws ServletException, IOException
  {
    try {
      boolean  result = handleSaveGiftlist(pRequest, pResponse);
      if (getFormError()) {
        return result;
      }
      String wishListId = getGiftlistManager().getWishlistId(getProfile().getRepositoryId());
      String giftListSiteId = getGiftlistManager().getGiftlistSite(getGiftlistId());
      for (RepositoryItem giftItem: (List<RepositoryItem>)getGiftlistManager().getGiftlistItems(wishListId)) {
        String giftItemSiteId = getGiftlistManager().getGiftlistItemSite(giftItem.getRepositoryId());
        if (getSiteGroupManager().doSitesShare(giftListSiteId, giftItemSiteId, getScope())) {
          moveGiftListItem(giftItem.getRepositoryId(), wishListId, getGiftlistId());
        }
      }
    } catch (IOException e) {
      addFormException(new DropletException(e.getLocalizedMessage()));
    } catch (RepositoryException e) {
      addFormException(new DropletException(e.getLocalizedMessage()));
    } catch (CommerceException e) {
      addFormException(new DropletException(e.getLocalizedMessage()));
    }
    return checkFormRedirect(null, getSaveGiftlistErrorURL(), pRequest, pResponse);
  }

  /**
   * This method iterates over all items of the gift list specified with <code>giftListId</code> property and removes all of them.
   * @param pRequest - Current HTTP request.
   * @param pResponse - Current HTTP response.
   * @return <code>false</code>
   * @throws ServletException if unable to get or remove a gift list item.
   */
  public boolean handleRemoveAllItems(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException
  {
    try
    {
      if (isLoggingDebug())
      {
        logDebug(MessageFormat.format("Start removing all items from giftlist ID=[{0}].", getGiftlistId()));
      }
      // Otherwise we have to actually check giftlist's owner.
      String owner = getGiftlistManager().getGiftlistOwner(getGiftlistId());
      boolean userOwnsGiftlist = getProfile().getRepositoryId().equals(owner);
      userOwnsGiftlist = userOwnsGiftlist || getGiftlistManager().getWishlistId(getProfile().getRepositoryId()).equals(getGiftlistId());
      // Allow to edit user's own gift lists only.
      if (userOwnsGiftlist)
      {
        if (isLoggingDebug())
        {
          logDebug("User owns giftlist, actually remove the items.");
        }
        // No need to begin transaction explicitly, as this class extends a TransactionalFormHandler.
        for (RepositoryItem giftItem: (List<RepositoryItem>)getGiftlistManager().getGiftlistItems(getGiftlistId()))
        {
          getGiftlistManager().removeItemFromGiftlist(getGiftlistId(), giftItem.getRepositoryId());
        }
      }
    } catch (RepositoryException e)
    {
      if (isLoggingError())
      {
        logError("Can't remove a giftlist item.", e);
      }
      throw new ServletException(e);
    } catch (CommerceException e)
    {
      if (isLoggingError())
      {
        logError("Can't remove a giftlist item.", e);
      }
      throw new ServletException(e);
    }
    return false;
  }

  /**
   * This method moves an existing giftlist item from a giftlist to a wishlist.
   * @param pRequest - Current HTTP request.
   * @param pResponse - Current HTTP response.
   * @return <code>false</code>
   * @throws ServletException if unable to create or remove a giftlist item.
   */
  public boolean handleMoveItemToWishList(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException
  {
    try
    {
      if (isLoggingDebug())
      {
        logDebug(MessageFormat.format("Start moving item ID=[{0}] from giftlist ID=[{1}] to wishlist.", getGiftItemId(), getGiftlistId()));
      }

      GiftlistManager manager = getGiftlistManager();
      String giftlistID = getGiftlistId();
      String itemID = getGiftItemId();

      // The user must own the gift list to be able to move items from it.
      boolean userHasAccessToGiftlist = getProfile().getRepositoryId().equals(manager.getGiftlistOwner(giftlistID));
      boolean giftlistContainsItem = manager.getGiftlistItems(giftlistID).contains(manager.getGiftitem(itemID));
      if (userHasAccessToGiftlist && giftlistContainsItem)
      {
        if (isLoggingDebug())
        {
          logDebug("User has an access to source giftlist, actually move the item.");
        }
        moveGiftListItem(itemID, giftlistID, manager.getWishlistId(getProfile().getRepositoryId()));
      }
    } catch (CommerceException e)
    {
      if (isLoggingError())
      {
        logError("Can't move the item to wishlist.", e);
      }
      throw new ServletException(e);
    } catch (RepositoryException e)
    {
      if (isLoggingError())
      {
        logError("Can't move the item to wishlist.", e);
      }
      throw new ServletException(e);
    }
    return false;
  }

  /**
   * This method copies an existing gift item to user's wish list.
   * @param pRequest - Current HTTP request.
   * @param pResponse - Current HTTP response.
   * @return <code>false</code>
   * @throws ServletException If unable to create a gift item.
   */
  public boolean handleCopyItemToWishList(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException
  {
    try
    {
      if (isLoggingDebug())
      {
        logDebug(MessageFormat.format("Start copying item ID=[{0}] from giftlist ID=[{1}] to wishlist.", getGiftItemId(), getGiftlistId()));
      }

      GiftlistManager manager = getGiftlistManager();
      String giftlistID = getGiftlistId();
      String itemID = getGiftItemId();

      boolean userHasAccessToGiftlist = manager.isGiftlistPublic(giftlistID);
      userHasAccessToGiftlist = userHasAccessToGiftlist || getProfile().getRepositoryId().equals(manager.getGiftlistOwner(giftlistID));
      boolean giftlistContainsItem = manager.getGiftlistItems(giftlistID).contains(manager.getGiftitem(itemID));
      if (userHasAccessToGiftlist && giftlistContainsItem)
      {
        if (isLoggingDebug())
        {
          logDebug("User has an access to source giftlist, actually copy the item.");
        }
        copyGiftListItem(itemID, giftlistID, manager.getWishlistId(getProfile().getRepositoryId()));
      }
    } catch (CommerceException e)
    {
      if (isLoggingError())
      {
        logError("Can't copy the item to wishlist.", e);
      }
      throw new ServletException(e);
    } catch (RepositoryException e)
    {
      if (isLoggingError())
      {
        logError("Can't copy the item to wishlist.", e);
      }
      throw new ServletException(e);
    }
    return false;
  }

  /**
   * This method moves an existing giftlist item from one giftlist to another one.
   * This method actually creates an exact copy of the existing giftlist item and then removes the item specified.
   * @param pRequest - Current HTTP request.
   * @param pResponse - Current HTTP response.
   * @return <code>false</code>
   * @throws ServletException if unable to create or remove a giftlist item.
   */
  public boolean handleMoveItemToGiftList(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException
  {
    try
    {
      if (isLoggingDebug())
      {
        logDebug(MessageFormat.format("Start moving item ID=[{0}] from giftlist ID=[{1}] to giftlist ID=[{2}].",
                getGiftItemId(), getSourceGiftlistId(), getDestinationGiftlistId()));
      }

      GiftlistManager manager = getGiftlistManager();
      String sourceID = getSourceGiftlistId();
      String destinationID = getDestinationGiftlistId();
      String itemID = getGiftItemId();

      // The user must own the source gift list to be able to move item from it.
      boolean userHasAccessToSource = getProfile().getRepositoryId().equals(manager.getGiftlistOwner(sourceID));
      // If source giftlist is user's wishlist, then it's accessible.
      userHasAccessToSource = userHasAccessToSource || manager.getWishlistId(getProfile().getRepositoryId()).equals(sourceID);

      // Check if the user owns destination giftlist.
      boolean userHasAccessToDestination = getProfile().getRepositoryId().equals(manager.getGiftlistOwner(destinationID));
      boolean sourceContainsItem = manager.getGiftlistItems(sourceID).contains(manager.getGiftitem(itemID));
      if (userHasAccessToSource && userHasAccessToDestination && sourceContainsItem)
      {
        if (isLoggingDebug())
        {
          logDebug("User has an access to both source and destination giftlists, actually move the item.");
        }
        // Source giftlist is read-accessible for user and destination giftlist is write-accessible.
        // So it's safe to move the item.
        moveGiftListItem(itemID, sourceID, destinationID);
      }
    } catch (RepositoryException e)
    {
      if (isLoggingError())
      {
        logError("Can't move the item to wishlist.", e);
      }
      throw new ServletException(e);
    } catch (CommerceException e)
    {
      if (isLoggingError())
      {
        logError("Can't move the item to wishlist.", e);
      }
      throw new ServletException(e);
    }
    return false;
  }

  /**
   * This method copies existing gift item to another gift list.
   * @param pRequest - Current HTTP request.
   * @param pResponse - Current HTTP response.
   * @return <code>false</code>
   * @throws ServletException If unable to create a gift item.
   */
  public boolean handleCopyItemToGiftList(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException
  {
    try
    {
      if (isLoggingDebug())
      {
        logDebug(MessageFormat.format("Start copying item ID=[{0}] from giftlist ID=[{1}] to giftlist ID=[{2}].",
                getGiftItemId(), getSourceGiftlistId(), getDestinationGiftlistId()));
      }

      GiftlistManager manager = getGiftlistManager();
      String sourceID = getSourceGiftlistId();
      String destinationID = getDestinationGiftlistId();
      String itemID = getGiftItemId();

      // Check if the user actually has access to the source giftlist.
      boolean userHasAccessToSource = manager.isGiftlistPublic(sourceID);
      // If the user owns this giftlist, then it's fully accessible.
      userHasAccessToSource = userHasAccessToSource || getProfile().getRepositoryId().equals(manager.getGiftlistOwner(sourceID));
      // If source giftlist is user's wishlist, then it's accessible.
      userHasAccessToSource = userHasAccessToSource || manager.getWishlistId(getProfile().getRepositoryId()).equals(sourceID);

      // Check if the user owns destination giftlist.
      boolean userHasAccessToDestination = getProfile().getRepositoryId().equals(manager.getGiftlistOwner(destinationID));
      boolean sourceContainsItem = manager.getGiftlistItems(sourceID).contains(manager.getGiftitem(itemID));
      if (userHasAccessToSource && userHasAccessToDestination && sourceContainsItem)
      {
        if (isLoggingDebug())
        {
          logDebug("User has an access to both source and destination giftlists, actually copy the item.");
        }
        // Source giftlist is read-accessible for user and destination giftlist is write-accessible.
        // So it's safe to copy the item.
        copyGiftListItem(itemID, sourceID, destinationID);
      }
    } catch (RepositoryException e)
    {
      if (isLoggingError())
      {
        logError("Can't copy the item to wishlist.", e);
      }
      throw new ServletException(e);
    } catch (CommerceException e)
    {
      if (isLoggingError())
      {
        logError("Can't copy the item to wishlist.", e);
      }
      throw new ServletException(e);
    }
    return false;
  }

  public boolean handleAddItemToWishList(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException, CommerceException
  {
    GiftlistManager manager = getGiftlistManager();
    setGiftlistId(manager.getWishlistId(getProfile().getRepositoryId()));
    return handleAddItemToGiftlist(pRequest, pResponse);
  }

  @Override
  public void preRemoveItemsFromGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
    super.preRemoveItemsFromGiftlist(pRequest, pResponse);
    if (getRemoveGiftitemIds() == null || getRemoveGiftitemIds().length == 0)
    {
      // iOS REST client can't set POST parameters of array type, so we have to user plain String
      // and then update actual form handler's parameter.
      setRemoveGiftitemIds(new String[] {getGiftItemId()});
    }
  }

  @Override
  public String saveGiftlist(String pProfileId) throws CommerceException
  {
    // Save resulting giftlist ID, it will be printed back to client.
    setGiftlistId(super.saveGiftlist(pProfileId));
    return getGiftlistId();
  }

  @Override
  public void postSaveGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
    // Super-implementation clears giftlistId property value, but we need it. So preserve it for future use.
    String giftListID = getGiftlistId();
    super.postSaveGiftlist(pRequest, pResponse);
    setGiftlistId(giftListID);
  }

  private void moveGiftListItem(String pGiftItemID, String pSourceGiftlistID, String pDestinationGiftlistID) throws CommerceException, RepositoryException
  {
    GiftlistManager manager = getGiftlistManager();
    manager.addCatalogItemToGiftlist(manager.getGiftlistItemCatalogRefId(pGiftItemID),
            manager.getGiftlistItemProductId(pGiftItemID),
            pDestinationGiftlistID,
            manager.getGiftlistItemSite(pGiftItemID),
            manager.getGiftlistItemQuantityDesired(pGiftItemID));
    getGiftlistManager().removeItemFromGiftlist(pSourceGiftlistID, pGiftItemID);
  }

  private void copyGiftListItem(String pGiftItemID, String pSourceGiftlistID, String pDestinationGiftlistID) throws CommerceException, RepositoryException
  {
    GiftlistManager manager = getGiftlistManager();
    manager.addCatalogItemToGiftlist(manager.getGiftlistItemCatalogRefId(pGiftItemID),
            manager.getGiftlistItemProductId(pGiftItemID),
            pDestinationGiftlistID,
            manager.getGiftlistItemSite(pGiftItemID),
            manager.getGiftlistItemQuantityDesired(pGiftItemID));
  }
}
