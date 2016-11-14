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
var z6 = "'product'.'chair'";
//var z4 = "v1<-'product'.(*-'chair')";
//var z5 = "v1<-(v2.range & v3.range))";
//var z7 = "z1.v1<-(* \\ {'year','sales'}).*";

var constraints1 = "location='UK'";
var constraints2 = "state='IL',city='Urbana'";
//var constraints3 = "product=‘chair’ AND zip LIKE ‘02\\d{3}’";

var viz1 = "bar.(y=agg('sum'))";
var viz2 = "bar.(x=bin(20),y=agg('sum'))";
var viz3 = "t1<-{bar,dotplot}.(x=bin(20),y=agg('sum'))";
var viz4 = "s1<-bar.{(x=bin(20),y=agg('sum')),(x=bin(30),y=agg('sum')),(x=bin(40),y=agg('sum'))}";

var process1 = "v3<-R(10,v2,f3)";
var process2 = "v2<-argmin_v1[k='100']D(f1,f2)"; // add _ axis variables
var process3 = "x2,y2<-argmax_x1,y1[k=10]D(f1,f2)";

/*
parseName(name2);
parseX(x2);
parseY(y2);
parseZ(z2);
parseConstraints(constraints2);
parseViz(viz2);
parseProcess(process2);
*/

/*
console.log(parseName(name2));
console.log(parseX(x1));
console.log(parseY(y2));
console.log(parseZ(z3));
console.log(parseConstraints(constraints2));
console.log(parseViz(viz4));
console.log(parseProcess(process3));

*/

//==============================================================
function parseName(input) {
	var restr = orExp([
			recExp(symVal('-')),
			recExp(symVal('\\*'))+recExp(varVal()),
			recExp(varVal())
		]);

	var re = new RegExp(restr);
    var found = input.match(re);
    console.log(found);
    try {
        var name = {
            output: (found[2] != undefined),
            sketch: (found[1] != undefined),
            name: found[3] || found[4]
        };
    }
    catch(err) {
        console.error("Name Column Syntax Error: "+err);
        return undefined;
    }
    return name;
}

//==============================================================
function parseX(input) {
	var restr = orExp([
			recExp(conVal())+'$',
			getExp(lstExp(varVal()), setExp(conVal())),
			recExp(varVal())+'$',
			lstExp(conVal())

		]);

	var re = new RegExp(restr);
    var found = input.match(re);
    console.log(found);
    try {
        var x = {
            variable: found[2] || found[4],
            attributes: parseList(found[1] || found[3] || found[5])
        };
    }
    catch(err) {
        console.error("X Column Syntax Error: "+err);
        return undefined;
    }
    return x;
}
//==============================================================
function parseY(input) {
	var restr = orExp([
			recExp(conVal())+'$',
			getExp(lstExp(varVal()), setExp(conVal())),
			recExp(varVal())+'$',
			lstExp(conVal())
		]);

	var re = new RegExp(restr);
    var found = input.match(re);
    console.log(found);
    try {
        var y = {
            variable: found[2] || found[4],
            attributes: parseList(found[1] || found[3] || found[5])
        };
    }
    catch(err) {
        console.error("Y Column Syntax Error: "+err);
        return undefined;
    }
    return y;
}

//==============================================================
function parseZ(input) {
	var restr = orExp([
					recExp(varVal())+'$',
					recExp(conVal())+'.'+recExp(conVal())+'$',
					recExp(conVal())+'.'+setExp(conVal())+'$',
					getExp(orExp([
								lstExp(varVal()),
								recExp(varVal()+'.'+varVal())
							]),
							orExp([
								recExp(conVal())+'.'+orExp([
												recExp(symVal('\\*')),
												recExp(conVal()),
												setExp(conVal())
											])
							])
					),
					parExp(),
					recExp(conVal())+'.'+orExp([
												recExp(symVal('\\*')),
												recExp(conVal()),
												setExp(conVal())
											])
				]);
	var re = new RegExp(restr);
    var found = input.match(re);
    console.log(found);
    try {
        var z = {
            variable: found[1] || found[6] || found[7],
            attribute: found[2] || found[4] || found[8] || found[12],
            values: parseList(found[3] || found[5] || found[9] || found[10] || found[11] || found[13] || found[14]),
            expression: undefined //need to add parser for expression later
        };
    }
    catch(err) {
        console.error("Z Column Syntax Error: "+err);
        return undefined;
    }
    return z;
}

//==============================================================
function parseConstraints(input) {
	var splits = parseList(input);
	var result = [];
	var restr = opExp(recExp(orExp([
			intVal(),
			conVal(),
			varVal()
		])));
	var re = new RegExp(restr);
    for (var i = 0; i < splits.length; i++) {
        var found = splits[i].match(re);
     	console.log(found);
        try {
            result.push({
                key: found[1],
                operator: found[2],
                value: found[3]
            });
        }
        catch(err) {
            console.error("Constraints Column Syntax Error: "+err);
            return undefined;
        }
    }
    return result;
}

//============================================================== need fixing for case set
function parseViz(input) {
	var restr = orExp([
			orExp([
				recExp(vizVal()),
				getExp(recExp(varVal()), recExp(vizVal())),
				getExp(recExp(varVal()), setExp(vizVal()))

			])+"."+
			orExp([
				parExp(lstExp(opExp(funExp(recExp(orExp([intVal(), conVal()])))))),
				setExp(parExp(lstExp(opExp(funExp(recExp(orExp([intVal(), conVal()])))))))
				]),

		]);
	var re = new RegExp(restr);
	var found = input.match(re);
    console.log(found);
    try {
        var viz = {
        	variable: found[4] || found[2],
            type: parseList(found[1] || found[3]),
            parameters: parseList(found[6] || found[15])
        }
    }
    catch(err) {
        console.error("Viz Column Syntax Error: "+err);
        return undefined;
    }
    return viz;
}

//==============================================================
function parseProcess(input) {
	var restr = getExp(lstExp(varVal()), orExp([
											funExp(lstExp(orExp([intVal(), varVal()]))),
											proExp(lstExp(orExp([varVal()])))
										])
				);

	var re = new RegExp(restr);
    var found = input.match(re);
	console.log("process");
    console.log(found);
    try {
		var processe = undefined;

		if (found[4] != undefined) {
	        processe = {
				/*
	            variables: parseList(found[1]),
	            method: found[2] || found[4],
	            axis: found[5],
	            count: found[9],
	            metric: found[13],
	            arguments: parseList(found[3]||found[14])
				*/
				variables: parseList(found[1]),
				method: found[13],
				axisList1: parseList(found[5]),
				axisList2: [],
				count: found[9],
				metric: found[2] || found[4], // found[2] should support IncTrends, found[4] supports DEuclidean
				arguments: parseList(found[3]||found[14]) // found[3] = (f1) found[14] = (f1,f2)
	        }
		}
		else if (found[15] != undefined) {
			processe = {
				// cross product case!
				variables: parseList(found[1]),
				method: found[25],
				axisList1: parseList(found[16]),
				axisList2: parseList(found[17]),
				count: found[21],
				metric: found[15],
				arguments: parseList(found[26])
			}
		}
    }
    catch(err) {
        console.error("Process Column Syntax Error: "+err);
        return undefined;
    }
    return processe;
}

//==============================================================
function parseList(input) {
    if (input === undefined) {
        return [];
    }
    var result = [];
    result = input.split(",");
    return result;

}
