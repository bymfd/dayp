
const electron =require("electron");
const {ipcRenderer} = electron;
window.$ = window.jquery = require('./node_modules/jquery');



var dataval;
var last;
        window.$(document).ready(function() {
            ipcRenderer.on('a', function (event,store) {

                if (store.length>0) {
                    dataval = store;
                    last =store;
                }
                else {
                    dataval = last;
                }
                var html_tablify = require('html-tablify');
                var options = {
                    border:2,
                    data: dataval
                };
                var html_data = html_tablify.tablify(options);
                console.log(html_data);
                window.document.getElementById("table").innerHTML = html_data;


            });


        });
