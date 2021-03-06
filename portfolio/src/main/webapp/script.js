// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);

/** Fetches fruit data and uses it to create a chart. */
function drawChart() {
  fetch('/fruit-data').then(response => response.json())
  .then((fruitVotes) => {
    var data = new google.visualization.DataTable();
    data.addColumn('string', 'Fruits');
    data.addColumn('number', 'Count');
    Object.keys(fruitVotes).forEach((fruit) => {
      data.addRow([fruit, fruitVotes[fruit]]);
    });

    const options = {
      'title': 'Favourite Fruits',
      'width':500,
      'height':400
    };

    var chart = new google.visualization.PieChart(
      document.getElementById('chart-container'));
    chart.draw(data, options);
  });
}

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
    ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!', 'YareYare'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

/** 
 * Adds a random fact about me to the page.
 */
function addRandomFact() {
  const facts = 
    ['I really like mangoes', 'I lived in Singapore for 10 years', 'I am left handed'];

  const fact = facts[Math.floor(Math.random() * facts.length)];

  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}

/**
 * Adds server response to the DOM.
 */
function getHelloResponse() {
  fetch('/data').then(response => response.text()).then((quote) => {
    document.getElementById('response-container').innerText = quote;
  });
}

/**
 * Fetches comment data and displays to the portfolio page.
 */
function getJson() {
  var limit = document.getElementById("limit").value;
  fetch(('/data?limit=').concat(limit)).then(response => response.text()).then((message) => console.log(message));

  // Add comments to the page
  fetch(('/data?limit=').concat(limit)).then(response => response.json()).then((m) => {
    const messagesListElement = document.getElementById('messages-container');
    messagesListElement.innerHTML = '';

    m.forEach((message) => {
      messagesListElement.appendChild(createListElement(message));
    })
  });
}

/**
 * Creates an <li> element containing text.
 */
function createListElement(comment) {
  const liElement = document.createElement('li');
  liElement.className = 'comment';

  const nameElement = document.createElement('span');
  nameElement.innerText = comment.name;

  const seperatorElement = document.createElement('span');
  seperatorElement.innerText = ' commented: ';

  const textElement = document.createElement('span');
  textElement.innerText = comment.text;

  liElement.appendChild(nameElement);
  liElement.appendChild(seperatorElement)
  liElement.appendChild(textElement);

  return liElement;
}

/**
 * Delete data from datastore and remove deleted comments from page.
 */
function deleteComments() {
  fetch('/delete-data', {
    method: 'POST'
  }).then(getJson());
}

/** Creates a map and adds it to the page. */
function createMap() {
  const map = new google.maps.Map(
    document.getElementById('map'), {
      center: {lat: -27.470125, lng: 153.021072}, 
      zoom: 13,
    });

    addLandmark(
      map, -27.491998032, 153.007666636, 'University of Queensland',
      'This is where I go to University.')

    addLandmark(
      map, -27.4777, 153.0214, 'Gelare South Bank',
      'Waffle Good')
}

/** Adds a marker that shows an info window when clicked. */
function addLandmark(map, lat, lng, title, description) {
  const marker = new google.maps.Marker(
      {position: {lat: lat, lng: lng}, map: map, title: title});

  const infoWindow = new google.maps.InfoWindow({content: description});
  marker.addListener('click', () => {
    infoWindow.open(map, marker);
  });
}

