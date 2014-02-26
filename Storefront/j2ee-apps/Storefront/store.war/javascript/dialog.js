dojo.provide("atg.store.Dialog");

dojo.declare(
  "atg.store.Dialog", 
  [dijit.Dialog],
  {
            
      _position: function() {
               
         if (!dojo.hasClass(dojo.body(), "dojoMove")) {
             var targetNode = this.domNode,
             targetWindow = dojo.window.getBox(),
             p = this._relativePosition,
             bb = p ? null: dojo._getBorderBox(targetNode),
             
             l = Math.floor(targetWindow.l + (p ? p.x: (targetWindow.w - bb.w) / 2)),
             t = Math.floor((targetWindow.h - bb.h) / 2);
             
             if(bb.h >= targetWindow.h) {
               dojo.style(targetNode, {
                   position: 'absolute',
                   left: l + "px",
                   top: "20px"
                });
             } else {
               dojo.style(targetNode, {
                  position: 'fixed',
                  left: l + "px",
                  top: t + "px"
               });
             }
         }
               
      }
            
  });