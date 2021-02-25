const electron = require("electron");
const url= require("url");
const path = require("path");
const {app,BrowserWindow,ipcMain } = electron;

let mainwindow;
const scanner = require('node-wifi-scanner');

app.on('ready' ,()=> {



    mainwindow= new BrowserWindow({   webPreferences: {
            nodeIntegration: true
        }});

    mainwindow.loadURL(
        url.format({
            pathname: path.join(__dirname,"pages","main.html"),
            protocol:"file:",
            slashes:true

        })
    );


    //spesifik mac adresi gelirse
    ipcMain.on("macad",(err,macad)=>{

        console.log(macad);
        telemetry_window= new BrowserWindow({ parent:mainwindow,   webPreferences: {
                nodeIntegration: true,
                modal:true,
            }});


        telemetry_window.loadURL(
            url.format({
                pathname: path.join(__dirname,"pages","telemetry.html"),
                protocol:"file:",
                html:"<h3>xzdjkaskjsad</h3>",
                slashes:true

            })
        );

      telemetry_window.webContents.send('amcad_for_telemetry', macad);



    })

async function scana() {
    scanner.scan((err, networks) => {
        if (err) {
            console.error(err);
            return;
        }


         mainwindow.webContents.send('a', networks);





    });

}



setInterval(scana, 500);




})































