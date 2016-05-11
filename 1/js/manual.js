$(function(){
    // manual
	$('#openmanual').bind('click', function() {

		var features = "location=yes, menubar=no, status=yes, scrollbars=yes, resizable=yes, toolbar=yes";
		var width = 700;
		var height = 500;
		if (width) {
			if (window.screen.width > width)
				features += ", left="
						+ (window.screen.width - width) / 2;
			else
				width = window.screen.width;
			features += ", width=" + width;
		}
		if (height) {
			if (window.screen.height > height)
				features += ", top="
						+ (window.screen.height - height) / 2;
			else
				height = window.screen.height;
			features += ", height=" + height;
		}
		window.open($('#openmanual').attr('href'), 'SmoothCSVマニュアル', features);
		return false;
	});

});