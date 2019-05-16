'use strict';

/*eslint-disable */
var traversefolder = require('./traverseFolder');
var fs = require('fs');

var _require = require('url'),
    URL = _require.URL;

var path = require('path');

var dirPath = path.resolve(__dirname, './src');
console.log(dirPath);

// 遍历文件夹，替换所有改变路径
traversefolder(dirPath).then(function (files) {
  // console.log(files);
  files = files.filter(function (one) {
    return (/(.*?)\.js$/i.test(one)
    );
  });
  // console.log(files);
  files.forEach(function (one) {
    fs.readFile(one, 'utf8', function (err, data) {
      if (err) {
        console.log(err);
      } else {
        data = data.replace(/choerodon-front-boot/g, function (match) {
          console.log('替换', match, '为', '@choerodon/boot');
          return '@choerodon/boot';
        });
        // console.log(data);
        fs.writeFile(one, data, function (err) {
          if (err) {
            console.log(err);
          }
        });
      }
    });
  });
})['catch'](function (e) {
  return console.error(e);
});