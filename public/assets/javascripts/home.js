
$(".flip-thing > li").each(function(i,el){
	var number = parseFloat($(el).text());
	$(el).html("").addClass("num-"+number);
	$(el).append("<span class='none'/>");
	for (var i=0;i<=number;i++){
		$(el).append("<span class='back-"+i+"'/>");
		$(el).append("<span class='num-"+i+"'/>");
	}
});


;(function(){

window.requestAnimFrame = (function(){
	return	window.requestAnimationFrame			 ||
			window.webkitRequestAnimationFrame ||
			window.mozRequestAnimationFrame		||
			window.msRequestAnimationFrame		 ||
			window.oRequestAnimationFrame
})();

window.vendor = "",
	div = document.createElement('div'),
	props = ['transform', 'WebkitTransform',
			'MozTransform', 'OTransform', 'msTransform'],
	i = 0,
	cssTransform = false;
while (props[i]) {
	if (props[i] in div.style) {
		cssTransform = true;
		vendor = props[i].replace(/transform/i,'');
		vendor = vendor.toLowerCase();
		if(cssTransform && vendor) vendor = "-" + vendor + "-";
		break;
	}
	i++;
}


if (!!('ontouchstart' in window) || !requestAnimFrame || !cssTransform) return false;

document.body.className = "animate";

var lastPosition = -10,
	wHeight = window.innerHeight,

	title = $("#header h1"),
	scrollDown = $("#scroll-down"),

	steps = $("#steps"),
	mountains2 = $("#mountains2"),
	mountains = $("#mountains"),
	clouds = $("#clouds"),
	clouds2 = $("#clouds2"),
	clouds3 = $("#clouds3"),
	cloudsF = $("#cloudsF"),
	cloudsM = $("#m1"),

	group1 = $(".group1"),

	land = $("#land"),

	flip = $(".flip-thing"),

	loop = function(){
		if (lastPosition == window.pageYOffset) {
			requestAnimFrame(loop);
			return false;
		} else lastPosition = window.pageYOffset;

		if (window.pageYOffset < 500) {
			title.css('opacity',1);
			scrollDown.css('opacity',1);
			mountains.css('opacity',1);
			mountains2.css('opacity',1);

			title.css(vendor+'transform', "translate3d(0, "+ (window.pageYOffset/-3.8) +"px,0)" );
			title.css('opacity', 1.2-(window.pageYOffset/400) );
			scrollDown.css('opacity', 1-(window.pageYOffset/100) );

			clouds.css(vendor+'transform', "translate3d(0, "+ (window.pageYOffset/-1.2) +"px,0)" );
			cloudsF.css(vendor+'transform', "translate3d(0, "+ (window.pageYOffset/-1.2) +"px,0)" );
			cloudsM.css(vendor+'transform', "translate3d(0, "+ (window.pageYOffset/-1.2) +"px,0)" );
			clouds2.css(vendor+'transform', "translate3d(0, "+ (window.pageYOffset/-1.8) +"px,0)" );
			clouds3.css(vendor+'transform', "translate3d(0, "+ (window.pageYOffset/-3) +"px,0)" );

			mountains.css(vendor+'transform', "translate3d(0, "+ (window.pageYOffset/-1.2) +"px,0)" );
			mountains2.css(vendor+'transform', "translate3d(0, "+ (window.pageYOffset/-2) +"px,0)" );

//			land.css(vendor+'transform', "translate3d(0, "+ (window.pageYOffset/-6) +"px,0)" )

		} else {
			title.css('opacity',0);
			scrollDown.css('opacity',0);

			mountains.css('opacity',0);
			mountains2.css('opacity',0);

			clouds.css(vendor+'transform', "translate3d(0, -416px,0)" );
			cloudsF.css(vendor+'transform', "translate3d(0, -416px,0)" );
			cloudsM.css(vendor+'transform', "translate3d(0, -416px,0)" );
			clouds2.css(vendor+'transform', "translate3d(0, -277px,0)" );
			clouds3.css(vendor+'transform', "translate3d(0, -166px,0)" );
		}

		if (window.pageYOffset > 700	) {
			flip.addClass("start");
		}

		requestAnimFrame(loop);
	};

window.onresize = function(){
	wHeight = window.innerHeight;
}
loop();

}());

;(function(){

	// This one is different from the previous one:
	window.requestAnimFrame = (function(){
		return	window.requestAnimationFrame			 ||
				window.webkitRequestAnimationFrame ||
				window.mozRequestAnimationFrame		||
				window.msRequestAnimationFrame		 ||
				window.oRequestAnimationFrame			||
				function(f){
					setTimeout(f,60);
				};
	})();

	var routes = {
			write: function(el){
				var day = $(el).parents(".day").attr("data-day"),
					hour = $(el).attr("data-time"),
					room = $(el).hasClass("room1") ? "room1" : $(el).hasClass("room2") ? "room2" : $(el).hasClass("room3") ? "room3" : "room",
					title = escape($(el).find("h2").first().text().replace(/\ /g,"-"));
				window.location.hash = "#/" + day + "/" + room + "/" + hour + "/" + title
			},
			read: function(){
				var target = window.location.hash.split("/"),
					el = $(".day[data-day='"+target[1]+"'] ."+ target[2] +"[data-time='"+target[3]+"']").first();

					if (target.length > 2) {
						$(window).scrollTop( (el.offset().top - 100)	);
						el.trigger("click");
					}
			}
		}

	var hourSize = 100;
	$("#schedule .day").each(function(ø,root) {

		var times = $(root).attr("data-time").split("-"),
			daystart = times[0].split(":"),
			daystop = times[1].split(":"),
			daytop = (parseFloat(daystart[0]) + parseFloat(daystart[1]) / 60),
			dayheight = (parseFloat(daystop[0]) + parseFloat(daystop[1]) / 60) - daytop + 1;

			$(root).css("height", (hourSize * dayheight) + "px");

			var side = $("aside", root);
			for (i = parseFloat(daystart[0]) + 1; i <= daystop[0] ; i++) {
				// PM:
				side.append("<span class='time' data-time='"+i+":00-"+i+":00'>"+(i>12?(i-12)+" pm":i+" am")+"</span>")
				// Not PM :
				//side.append("<span class='time' data-time='"+i+":00-"+i+":00'>"+i+":00</span>")
			}

		$("[data-time]",root).each(function(ø, el) {
			var times = $(el).attr("data-time").split("-"),
				start = times[0].split(":"),
				stop = times[1].split(":"),
				top = (parseFloat(start[0]) + parseFloat(start[1]) / 60),
				height = (parseFloat(stop[0]) + parseFloat(stop[1]) / 60) - top;

			if (height < 120) {
				$(el).addClass("small");
			}

			$(el).css("top", (hourSize * (top - daytop)) + "px");
			$(el).css("height", (hourSize * height) + "px");
		});

	});

	$("#schedule .day").click(function(e){
		allTracks.removeClass("active");
			$("#details").slideUp("fast");
			$("#schedule").removeClass("open");
			window.location.hash = "/";
	});
	var allTracks = $("#schedule .tracks .track").click(function(e){
		e.preventDefault();
		
		if ( $(this).hasClass("tbd") ) {
			// exit if this session is still tbd
			return false;
		}
		
		if ( $(this).hasClass("active") ) {
			$(this).removeClass("active");
			$("#schedule").removeClass("open");
			$("#details").slideUp("fast");
			window.location.hash = "/";
			return false;
		}

		routes.write(this);

		$("#details").slideDown("fast");
		$("#schedule").addClass("open");

		allTracks.removeClass("active");
		$(this).addClass("active");

		$("#details .description").html( $(".details",this).html() );
		
		var times = $(this).attr("data-time").split("-"),
			timeWrap = $("#details .times").html(""),
			start = times[0].split(":"),
			stop = times[1].split(":"),
			top = (parseFloat(start[0]) + parseFloat(start[1]) / 60),
			height = (parseFloat(stop[0]) + parseFloat(stop[1]) / 60) - top;

		$("<span class='time'> "+times[0]+" </span>")
				.css("top", "50px")
				.appendTo(timeWrap);

		$("<span class='time'> "+times[1]+" </span>")
				.css("top", (hourSize * height + 50) + "px")
				.appendTo(timeWrap);

		timeWrap.css("background-position", " right -" + (top * hourSize - 50 ) + "px");
		return false;
	});

	// Scroll effect on schedule
	var lastPosition = -1,
		wHeight = 0,
		titles = [],
		details = {};
	function loop(){
		if (lastPosition == window.pageYOffset) {
			requestAnimFrame(loop);
			return false;
		} else lastPosition = window.pageYOffset;

		titles.each(function(i, title){
			if (lastPosition > title.top && lastPosition < title.bottom){
				title.el.className = "fixed";
			} else if (lastPosition > title.bottom) {
				title.el.className = "after";
			} else {
				title.el.className = "";
			}
		});

		if (lastPosition > details.top && lastPosition < details.bottom){
			details.el.className = "fixed";
		} else if (lastPosition > details.bottom) {
			details.el.className = "after";
		} else {
			details.el.className = "";
		}

		requestAnimFrame(loop);
	}

	window.onresize = function(){
		wHeight = window.innerHeight;
		titles = $(".day").map(function(i,el){
			return {
				el: $(el).find("header")[0],
				top: $(el).offset().top,
				bottom: $(el).offset().top + $(el).height() - 60
			}
		});
		details = $("#schedule").map(function(i,el){
			return {
				el: $("#details")[0],
				top: $(el).offset().top - wHeight + 300,
				bottom: $(el).offset().top + $(el).height() - wHeight + 240
			}
		})[0];
		//console.log(details)
	}
	window.onresize();

	loop();

	routes.read();


}());


;(function(){

if (!!('ontouchstart' in window)) return false

// MAP
var hotelPosition = new google.maps.LatLng(40.756914, -73.984873);
var mapOptions = {
	center: new google.maps.LatLng(40.756914, -73.984873),
	zoom: 14,
	mapTypeId: google.maps.MapTypeId.ROADMAP,
	scaleControl: false,
	scrollwheel: false,
	styles: [
		{
			"featureType": "landscape",
			"elementType": "geometry.fill",
			"stylers": [
				{ "color": "#e35750" }
			]
		},{
			"featureType": "poi",
			"elementType": "geometry.fill",
			"stylers": [
				{ "color": "#f77c7d" }
			]
		},{
			"featureType": "road.local",
			"stylers": [
				{ "weight": 0.5 },
				{ "color": "#a04444" }
			]
		},{
			"featureType": "water",
			"elementType": "geometry",
			"stylers": [
				{ "color": "#7fdff9" }
			]
		},{
			"featureType": "water",
			"elementType": "labels",
			"stylers": [
				{ "visibility": "off" }
			]
		},{
			"featureType": "transit",
			"stylers": [
				{ "visibility": "off" }
			]
		},{
			"featureType": "road",
			"elementType": "geometry.fill",
			"stylers": [
				{ "visibility": "on" },
				{ "color": "#ec7876" }
			]
		},{
			"featureType": "road",
			"elementType": "geometry.stroke",
			"stylers": [
				{ "color": "#764747" }
			]
		},{
			"featureType": "road",
			"elementType": "labels.text.fill",
			"stylers": [
				{ "visibility": "on" },
				{ "color": "#ffffff" }
			]
		},{
			"featureType": "road",
			"elementType": "labels.text.stroke",
			"stylers": [
				{ "visibility": "off" }
			]
		}
	]
};
var map = new google.maps.Map(document.getElementById("map"), mapOptions);
var marker = new google.maps.Marker({
	position: hotelPosition,
	map: map,
	title: 'Hudson Hotel'
});

}());
