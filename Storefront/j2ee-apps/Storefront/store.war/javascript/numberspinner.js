dojo.ready(function(){
    dojo.query("input[type=number]").forEach(function(inputElem){
      var ns = new dijit.form.NumberSpinner(
        {
          smallDelta:1, // arrow up/down increment/decrement value
          largeDelta:2, // page up/down increment/decrement value
          constraints :
          {
            min:parseInt(inputElem.getAttribute("min"), 10),
            max:parseInt(inputElem.getAttribute("max"), 10),
            places:0 // decimal places
          },
          value : inputElem.value,
          name : inputElem.name
        },
        inputElem
      );
    });
});