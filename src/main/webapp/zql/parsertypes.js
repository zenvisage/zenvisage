var name1 = "*f1";
var name2 = "f2";
var name3 = "-";

var x1 = "'year'";
var x2 = "x1<-{'time','location'}";

var y1 = "'sales'";
var y2 = "y1<-{'sales','profit'}";

var z1 = "v1";
var z2 = "v1<-'product'.*";
var z3 = "v1<-'product'.'chair'";
var z3 = "v1<-'product'.{'chair','desk'}";
var z4 = "v1<-'product'.(*-'chair')";
var z5 = "v1<-(v2.range & v3.range))";
var z6 = "'product'.'chair'";
var z7 = "z1.v1<-(* \\ {'year','sales'}).*";

var constraints1 = "location='UK'";
var constraints2 = "state='IL',city='Urbana'";
var constraints3 = "product=‘chair’ AND zip LIKE ‘02\\d{3}’";

var viz1 = "bar.(y=agg('sum'))";
var viz2 = "bar.(x=bin(20),y=agg('sum'))";
var viz3 = "t1<-{bar,dotplot}.(x=bin(20),y=agg('sum'))";
var viz4 = "s1<-bar.{(x=bin(20),y=agg('sum')),(x=bin(30),y=agg('sum')),(x=bin(40),y=agg('sum'))}";

var process1 = "v3<-R(10,v2,f3)";
var process2 = "v2<-argmin_v1[k='100']D(f1,f2)"; // add _ axis variables
var process3 = "x2,y2<-argmax_x1,y1[k=10]D(f1,f2)";

// a variable value
function varVal() {

	var restr = "(?:[a-z]\\d+)";
	return restr;
}

function intVal() {
	var restr = "(?:\\d+)"
	return restr;
}

// a valid predefined special purpose symbol value
function symVal(symbols) {
	var restr = "(?:"+symbols+")";
	return restr;
}

// constant value
function conVal() {

	var restr = "(?:'\\w*')";
	return restr;
}

// a valid operator symbol
function opVal() {
	var restr = orExp([
			symVal('\\+'),
			symVal('-'),
			symVal('>'),
			symVal('<'),
			symVal('='),
			symVal('>='),
			symVal('<='),
			symVal('\\\\'),
			symVal('&')
		]);
	return restr;
}

// list of allowed visualizations
function vizVal() {
	var restr = orExp([
			symVal('bar'),
			symVal('chart'),
			symVal('scatterplot'),
			symVal('dotplot')
		]);
	return restr;
}

// process optimization functions
function proVal() {
	var restr = orExp([
			symVal('argmax'),
			symVal('argmin'),
			symVal('argany'),
			symVal('Similar'),
			symVal('Dissimilar'),
			symVal('IncTrends'),
			symVal('DecTrends')

		]);
	return restr;
}

// process function primitives
function metVal() {
	var restr = orExp([
			symVal('T'),
			symVal('D'),
			symVal('R')
		]);
	return restr;
}

// a <- expression
function getExp(variable, expression) {
	var restr = "(?:"+variable+"<-"+expression+")";
	return restr;
}

// either or
function orExp(expressions) {
	var restr = "(?:"+"(?:"+expressions[0];
	if (expressions.length > 1) {
		for (var i = 1; i < expressions.length; i++) {
			restr += ")|(?:";
			restr += expressions[i];
		}
	}
	return restr+"))";
}

// a list of expressions within parenthesis
function parExp(expression) {
	var restr = "(?:\\("+expression+"\\))";
	return restr;
}

// a set of items, will record
function setExp(expression) {

	var restr = "(?:{("+expression+"(?:,"+expression+")*)})";
	return restr;

}

// a list or just one expressions, will record
function lstExp(expression) {

	var restr = "(?:("+expression+"(?:,"+expression+")*))";
	return restr;

}

// functions in zql will record
function funExp(expression) {
	var restr = "(?:(\\w+)\\("+expression+"\\))";
	return restr;
}


// process expression will record
function proExp(expression) {
	var restr_pairwise = "(?:("+proVal()+")(?:_\\{"+lstExp(varVal())+"\\})*(?:\\["+lstExp(opExp(recExp(orExp([intVal(), conVal()]))))+"\\])*"+funExp(expression)+")";
	var restr_cross = "(?:("+proVal()+")(?:_\\{"+lstExp(varVal())+"\\}x)(?:\\{"+lstExp(varVal())+"\\})*(?:\\["+lstExp(opExp(recExp(orExp([intVal(), conVal()]))))+"\\])*"+funExp(expression)+")";
	var restr = orExp([restr_pairwise, restr_cross]);
	//var restr = restr_pairwise;
	return restr;
}


// assignment expression, will record
function opExp(expression) {
	var restr = "(?:([*.\\w]+)("+opVal()+")"+expression+")";
	return restr;
}

// record the current expression or values
function recExp(expression) {
	return "("+expression+")";
}
