//Install express server
const express = require('express');
const path = require('path');

const app = express();

// Serve only the static files form the dist directory
app.use(express.static('./dist'));

// path to where the files are stored on disk
var FILES_DIR = path.join(__dirname, 'data')

app.get('/spa', (req, res) =>
  res.sendFile('index.html', { root: 'dist/' }),
);

app.get('/download/:file', function (req, res, next) {
  const file_name = FILES_DIR + '/' + req.params.file;
  //res.attachment(file_name);
  res.download(file_name, function (err) {
    if (!err) return; // file sent
    if (err.status !== 404) return next(err); // non-404 error
    // file for download not found
    res.statusCode = 404;
    res.send('Cant find that file, sorry!');
  });
});


var portno = 3000; // Port number to use
// Start the app 
var server = app.listen(portno, function () {
  var port = server.address().port;
  console.log(
    "Listening at http://localhost:" +
    port +
    " exporting the directory " +
    __dirname
  );
});