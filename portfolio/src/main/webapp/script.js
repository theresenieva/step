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
