/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


var links;
var nodes = {};
var link;
var force;
var svg;
var node;
var conceptos=[];
function grafo() {
    // http://blog.thomsonreuters.com/index.php/mobile-patent-suits-graphic-of-the-day/


    nodes = {};

    // Compute the distinct nodes from the links.
    links.forEach(function (l) {
        l.source = nodes[l.source] || (nodes[l.source] = {name: l.source});
        l.target = nodes[l.target] || (nodes[l.target] = {name: l.target});      
    });

    var width = 960,
            height = 500;

    force = d3.layout.force()
            .nodes(d3.values(nodes))
            .links(links)
            .size([width, height])
            .linkDistance(60)
            .charge(-300)
            .on("tick", tick)
            .start();

    svg = d3.select("#mydiv").append("svg")
            .attr("width", width)
            .attr("height", height);

    link = svg.selectAll(".link")
            .data(force.links())
            .enter().append("line")
            .attr("class", "link")
            .style("stroke-width", function(d) { return Math.sqrt(d.score); });;

    node = svg.selectAll(".node")
            .data(force.nodes())
            .enter().append("g")
            .attr("class", "node")
            .on("mouseover", mouseover)
            .on("mouseout", mouseout)
            .on("click", click)
            .call(force.drag);

    node.append("circle")
            .attr("r", 8);

    node.append("text")
            .attr("x", 12)
            .attr("dy", ".35em")
            .text(function (d) {
                return d.name;
            });

    function tick() {
        link
                .attr("x1", function (d) {
                    return d.source.x;
                })
                .attr("y1", function (d) {
                    return d.source.y;
                })
                .attr("x2", function (d) {
                    return d.target.x;
                })
                .attr("y2", function (d) {
                    return d.target.y;
                });

        node
                .attr("transform", function (d) {
                    return "translate(" + d.x + "," + d.y + ")";
                });
    }

    function mouseover() {
        d3.select(this).select("circle").transition()
                .duration(750)
                .attr("r", 16);  
    }
    
    function click(){
         v=d3.select(this).select("text");
         conceptos=conceptos.concat(v.text());
         word2vecFilterVector();
        
    }

    function mouseout() {
        d3.select(this).select("circle").transition()
                .duration(750)
                .attr("r", 8);
    }



}

function word2vecFilterVector() {
    json=document.getElementById("form:texto_are");
    concepto=document.getElementById("form:idConcepto").value;
    
    links=JSON.parse(json.value);
    links=links.filter(function (a) {
       var n1 = conceptos.includes(a.target);
       var n2 = conceptos.includes(a.source);
       return n1||n2;
    });
    document.getElementById("mydiv").innerHTML=""
    grafo();  
}


function word2vec(json) {
    json=document.getElementById("form:texto_are");
    
    links=JSON.parse(json.value);
    conceptos=[];
    grafo();  
}

function word2vecFilter() {
    json=document.getElementById("form:texto_are");
    concepto=document.getElementById("form:idConcepto").value;
    
    links=JSON.parse(json.value);
    links=links.filter(function (a) {
       return a.target === concepto || a.source === concepto;
    });
    conceptos=[];
    document.getElementById("mydiv").innerHTML=""
    grafo();  
}



function relaciones() {
    
    var doc=document.getElementById("form:nrdoc").value;
    var nro=document.getElementById("form:inputTopo").value;
    //var nro=$("#form:inputTopo").val();
    if(nro==''){
        alert("Seleccione el número de temas");
        return;
    }
   
    
    window.open('http://localhost:8890/Modelo_topicos?id_documento=' + doc + '&topicos='+nro, '_blank');
    /*
     $.ajax({
     
     url: 'http://localhost:8889/Modelo_topicos',
     type: 'POST',
     dataType: "html",
     
     data: {id_documento: idDoc, topicos: 2},
     
     complete: function (xhr, status) {
     alert(xhr.responseText);
     if (status === 'error' || !xhr.responseText) {
     alert("Error Nuevamete caballo");
     } else {
     var data = xhr.responseText;
     alert(data);
     }
     }
     });
     // $('.menuContainer').load('http://localhost:8889/Modelo_topicos');*/
    return false;
}   