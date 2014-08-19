    var map;
	function initialize() {
	  // Create a simple map.
	  map = new google.maps.Map(document.getElementById('map'), {
	    zoom: 13,
	    center: {lat: 45.47, lng: 9.18}
	  });
	
	  // Load a GeoJSON from the same server as our demo.
	  map.data.loadGeoJson('data/milan_trans.json');

        map.data.forEach(function(feature){
          console.log(feature.getProperty("OSMID"));

        });

	  map.data.setStyle({
		strokeColor: 'green',
		strokeWeight: 3
	  });

	  map.data.addListener('mouseover', function(e) {
      document.getElementById('info_box').textContent = e.feature.getProperty('OSMID');
//      var feature = map.data.feature();
//            feature.getProperty("OSMID");

          map.data.overrideStyle(e.feature, {strokeColor: 'red'});
	 });

    map.data.addListener('mouseout', function() {
            map.data.revertStyle();
    });

	}
      
	google.maps.event.addDomListener(window, 'load', initialize);
	  

		