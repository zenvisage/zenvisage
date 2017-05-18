console.log('slider')

$( function() {
	$( "#slider-range-max" ).slider({
		range: "max",
		min: 1,
		max: 10,
		value: 0,
		slide: function( event, ui ) {
			$( "#amount" ).val( ui.value );
			console.log(ui.value);
		}
	} );
	$( "#amount" ).val( $( "#slider-range-max" ).slider( "value" ) );
} );
