$( function() {
	$( "#slider-range-max" ).slider({
		range: "max",
		min: 0,
		max: 1,
		step:0.05,
		value: 0.5,
		slide: function( event, ui ) {
			$( "#amount" ).val( ui.value );
			console.log(ui.value);
		}
	} );
	$( "#amount" ).val( $( "#slider-range-max" ).slider( "value" ) );
} );
