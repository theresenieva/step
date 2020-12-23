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
    const statsListElement = document.getElementById('messages-container');
    statsListElement.innerHTML = '';

    for (i = 0; i < m.Messages.length; i++) {
      statsListElement.appendChild(createListElement('Comment ' + i.toString() + ': ' + m.Messages[i]));
    }
  });
}

/**
 * Creates an <li> element containing text.
 */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
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

  /* const uniMarker = new google.maps.Marker({
    position: {lat: -27.491998032, lng: 153.007666636},
    map: map,
    title: 'University of Queensland'
  });

  const waffleMarker = new google.maps.Marker({
    position: {lat: -27.4777, lng: 153.0214},
    map: map,
    title: 'Gelare South Bank'
  }); */
}
