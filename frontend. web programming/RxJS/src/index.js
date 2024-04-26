import { fromEvent } from 'rxjs';
import { ajax } from 'rxjs/ajax';
import { map, switchMap } from 'rxjs/operators';

const showDataButton = document.getElementById('show-data-btn');
const deleteButton = document.getElementById('delete-btn');
const dataContainer = document.getElementById('data-container');

fromEvent(showDataButton, 'click')
  .pipe(
    switchMap(() => ajax.getJSON('/files/dances.json')),
    map(data => {
      const div = document.createElement('div');
      div.textContent = JSON.stringify(data);
      return div;
    })
  )
  .subscribe(div => dataContainer.appendChild(div));

fromEvent(deleteButton, 'click')
  .pipe(
    map(() => dataContainer.lastChild),
    map(child => dataContainer.removeChild(child))
  )
  .subscribe();
