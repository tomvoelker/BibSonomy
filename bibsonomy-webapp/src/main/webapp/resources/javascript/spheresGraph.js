/**
 * Class for visualizing spheres
 */
function SphereDisplay() {
	this.w = this.getWidth();
	this.h = this.getHeight();
	
	if (!this.checkSVG()) {
		// browser doesn't support SVG
		$(".sphere").toggleClass("sphere_hidden");
		$("#spheresGraph").hide();
		return;
	}

    this.nodes = Array();

    this.ctrlPressed = false;

    var backref = this;
    document.onkeydown = function(e) {backref.cacheCtrlOn(e)};
    document.onkeyup = function(e) {backref.cacheCtrlOff(e)};
    
    this.force = d3.layout.force()
    	.gravity(0)
    	.friction(0.9)
    	.charge(function(d) { if (d.group==0) {return -50} else if (d.group==1) { return -100} else { return -50 } })
    	.linkDistance(this.linkDistanceMap)
    	.size([backref.w, backref.h]);
    
    this.vis = d3.select("#spheresGraph").append("svg");
    
    this.start();
};

/**
 * check whether browser supports SVG and thus d3.js
 */
SphereDisplay.prototype.checkSVG = function() {
	// SVG test taken from Modernizr 2.0
	if (!!document.createElementNS && !!document.createElementNS('http://www.w3.org/2000/svg','svg').createSVGRect) {
		// ok - browser supports SVG
		return true;
	}
	// browser doesn't seem to support SVG
	return false;
}

/**
 * determine the length of a given link
 *  
 * @param d the link
 * @param i the link's index
 * 
 * @return length of the given link
 */
SphereDisplay.prototype.linkDistanceMap = function(d,i) {
    if(d.source.group==0) {
    	return d.target._children ? d.source.size*3 : d.source.size*5;
    } else {
    	return 60;
    }
}


/**
 * start the visualizer
 */
SphereDisplay.prototype.start = function() {
    var backref = this;
    $.ajax( {
		type : "GET",
		url : "/json/spheres",
		dataType : "jsonp",
		jsonp : "callback",
		success : function(json) {
			
			console.log(json);
		    // Compute the distinct nodes from the links.
		    var mmap = Object();
		    
		    backref.nodes[0] = {name: "You", group: 0, size: 30, fixed: true, x: (backref.getWidth()/2), y: (backref.getHeight()/2), children: [], id: 0};
		    var i=1;
		    var j=0;
		    var sphereId;
		    var userId;
		    json.items.forEach(function(sphere) {
				sphereId = i++;
				backref.nodes[sphereId] = {name: sphere.name, group: 1, size: sphere.members.length, _children: [], id: sphereId};
		
				backref.nodes[0].children.push(backref.nodes[sphereId]);
		
				sphere.members.forEach(function(user) {
				    if (!(user.name in mmap)) {
						userId = i++;
						mmap[user.name] = userId;
						backref.nodes[userId] = {name: user.name, group: 2, size: 1, id: userId};
				    } else {
						userId = mmap[user.name];
				    }
				    backref.nodes[sphereId]._children.push(backref.nodes[userId]);
				});
		    });
		    backref.root = backref.nodes[0];
		    backref.update();
		}
    });	
}


/**
 * update the graph
 *  - add newly opened childs
 *  - remove nodes which were closed previously
 *  - add listener
 */
SphereDisplay.prototype.update = function() {
	
    var backref = this;
    var nodes = this.flatten(this.root),
    links = d3.layout.tree().links(nodes);

    // Restart the force layout.
    this.force.nodes(nodes)
		.links(links)
		.start();

    // Update the links…
    var link = this.vis.selectAll("line.link")
		.data(links, function(d) { var k1=d.source.id; var k2=d.target.id; return (k1+k2)*(k1+k2+1)*1/2+k2});

    // Enter any new links.
    link.enter().insert("svg:line", ".node")
		.attr("class", "link")
		.attr("x1", function(d) { return d.source.x; })
		.attr("y1", function(d) { return d.source.y; })
		.attr("x2", function(d) { return d.target.x; })
		.attr("y2", function(d) { return d.target.y; });

    // Exit any old links.
    link.exit().remove();

    // Update the nodes…
    var node = this.vis.selectAll("g.node")
    	.data(nodes, function(d) { return d.id; });

    node.select("circle")
		.style("fill", this.color);

    // Enter any new nodes.
    var nodeEnter = node.enter().append("svg:g")
		.attr("class", "node")
		.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
		.call(this.force.drag);

    nodeEnter.append("svg:circle") .on("click", 
    	function(d) {
    		if (d.group == 1) {
       			$(".sphere").hide();
       			$("#sphere_"+d.name).show();
    			backref.click(d)
    		}
    	}).on("mouseover", function(d) {
    		if (d.group==1) {
    			$(".sphere").hide();
    			$("#sphere_"+d.name).show();
    		}
    	}).attr("r", function(d){return backref.setRadius(d);}).style("fill", this.color)
	
		
	// Append the text to every node
    nodeEnter.append("svg:text")
    	.attr("id",function(d) { return "textElement_" + d.name })
    	.attr("x", function(d) { return d.size + 8 })
    	.attr("y", ".31em")	
    	.append("a")
    	.on("click", function(d) {
    		if(d.group == "1") {
    			$('html, body').animate({scrollTop: $("#sphere_" + d.name ).offset().top}, 'slow');
    		} 
    	}).on("mouseover", function(d) {
    		if (d.group==1) {
    			$(".sphere").hide();
    			$("#sphere_"+d.name).show();
    		}
    		this.style.cursor='pointer';
    	}).attr("xlink:href", function(d) {
    		if(d.group == "2") {
    			return "/user/" + d.name;
    		}
    	}).text( function(d) {
    		if (d.group!=0) {
    			return d.name;
    		} else {
    			return null;
    		}
    	});

    
    nodeEnter.append("title").text(function(d) { return d.name; });

    // Exit any old nodes.
    node.exit().remove();

    // Re-select for update.
    link = this.vis.selectAll("line.link");
    node = this.vis.selectAll("g.node");

    this.force.on("tick", function() {
    	
    	
		link.attr("x1", function(d) { return backref.fixWidth(d.source.x); })
		    .attr("y1", function(d) { return backref.fixHeight(d.source.y); })
		    .attr("x2", function(d) { return backref.fixWidth(d.target.x); })
		    .attr("y2", function(d) { return backref.fixHeight(d.target.y); });
		
		node.attr("transform", function(d) { return "translate(" + backref.fixWidth(d.x) + "," + backref.fixHeight(d.y) + ")"; });
		
		/*
        // soft-center the root node
        var k = .03;
        var nodes = backref.root;
        nodes.py += (backref.getHeight()/2 - nodes.py) * k;
        nodes.px += (backref.getWidth()/2 - nodes.px) * k;
        */ 
	});
}

/**
 * stop nodes from clipping
 */
SphereDisplay.prototype.fixWidth = function(x) {
	if (x<10) {
		return 10;
	} else if (x>this.getWidth()-10) {
		return this.getWidth()-10;
	}
	
	else return x;
}

/**
 * stop nodes from clipping
 */
SphereDisplay.prototype.fixHeight = function(y) {
	if (y<10) {
		return 10;
	} else if (y>this.getHeight()-10) {
		return this.getHeight()-10;
	}
	
	else return y;
}

/**
 * Color leaf nodes orange, and packages white or blue.
 */
SphereDisplay.prototype.color = function(d) {
    if (d.group==0) { 
    	return "#1f77b4";
    } else if (d.group==1) {
    	return "#aec7e8";
    } else {
    	return "#ff7f0e";
    }
}

/**
 * toggle children on click.
 */
SphereDisplay.prototype.click = function(d) {
    if (this.ctrlPressed) {
    	d.fixed = !d.fixed;
    } else if (d.children) {
    	d._children = d.children;
    	d.children = null;
    } else {
    	d.children = d._children;
    	d._children = null;
    }
    this.update();
}

/**
 * keep track of the ctrl-key's state
 */
SphereDisplay.prototype.cacheCtrlOn = function(event) {
    if (event.ctrlKey) {
    	this.ctrlPressed = true;
    }
}

/**
 * keep track of the ctrl-key's state
 */
SphereDisplay.prototype.cacheCtrlOff = function(event) {
    if (event.ctrlKey) {
    	this.ctrlPressed = false;
    }
}

/** 
 * returns a list of all nodes under the root
 */
SphereDisplay.prototype.flatten = function(root) {
    var nodes = [], 
    i = 0;

    function recurse(node) {
    	if(node.children) {
    		node.children.reduce(function(p, v) { return p + recurse(v); }, 0);
    	}
    	if(!node.id) {
    		 node.id = ++i;
    	}
    	
		nodes.push(node);
		return node.size;
    }
    recurse(root);
    return nodes;
}

/**
 * Get the width of the div, in which the Graph is placed.
 */
SphereDisplay.prototype.getWidth = function () {
	var temp = $("#spheresGraph").width(); 
	return temp;
}

/**
 * Get the height of the div, in which the Graph is placed.
 */
SphereDisplay.prototype.getHeight = function () {
	var temp = $("#spheresGraph").height();	
	return temp;
}

/**
 * Set the radius of a circle.
 */
SphereDisplay.prototype.setRadius = function(d) {
	
	var size = d.size*2+3;
	
	if(d.group == 1) {	
		if(size >= 13) {
			return 13;
		} else {
			return size;
		}
	} else {
		
		if(d.group == 2) {
			return d.size+3;
		}
		return d.size;
	}
}

/**
 * Function, to call a Sphere update
 */
SphereDisplay.prototype.externUpdate = function() {
	SphereDisplay.update();
}