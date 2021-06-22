








window.onload = function() {

var url = location.href;

var currentLink;

var param = location.search;


if(url.match('saleDesc')) {
	currentLink = document.getElementById('saleDescLink');
	currentLink.removeAttribute('href');
}
else if(url.match('priceDesc')) {
	currentLink = document.getElementById('priceDescLink');
	currentLink.removeAttribute('href');
}
else if(url.match('priceAsc')) {
	currentLink = document.getElementById('priceAscLink');
	currentLink.removeAttribute('href');
}
else if(  (url.match('list/?') && url.endsWith('=') || param.match('page'))  || param == ""){
	currentLink = document.getElementById('newArrivals');
	currentLink.removeAttribute('href');
}


}