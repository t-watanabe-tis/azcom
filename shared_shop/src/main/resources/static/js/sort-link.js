








window.onload = function() {

var url = location.href;

var currentLink;

var param = location.search;


if(url.match('saleDesc')) {
	currentLink = document.getElementById('sale_desc_link');
	currentLink.removeAttribute('href');
}
else if(url.match('priceDesc') || url.match('PriceDesc')) {
	currentLink = document.getElementById('price_desc_link');
	currentLink.removeAttribute('href');
}
else if(url.match('priceAsc') || url.match('PriceAsc'))  {
	currentLink = document.getElementById('price_asc_link');
	currentLink.removeAttribute('href');
}
else if(  (url.match('list/?') && url.endsWith('=') || param.match('page'))  || param == "" || url.match('NewArrival')){
	currentLink = document.getElementById('new_arrivals_link');
	currentLink.removeAttribute('href');
}


}