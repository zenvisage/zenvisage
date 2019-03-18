$( function() {
    $( "#binning-slider" ).slider({
        range: "max",
        min: 0,
        max: 10,
        step:0.5,
        value: 1.0,
        slide: function( event, ui ) {
            $( "#binning-amount" ).val( ui.value );
            console.log(ui.value);
        }
    } );
    $( "#binning-amount" ).val( $( "#binning-slider" ).slider( "value" ) );
} );