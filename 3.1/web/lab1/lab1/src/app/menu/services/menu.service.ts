import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { MenuItem } from '../models/menu.model';
import {
  Firestore,
  collectionData,
  collection,
  addDoc,
  doc,
  updateDoc,
  deleteDoc,
  docData,
  CollectionReference,
  DocumentReference,
} from '@angular/fire/firestore';

@Injectable({
  providedIn: 'root',
})
export class MenuService {
  firestore: Firestore = inject(Firestore);

  private menuCollection: CollectionReference = collection(
    this.firestore,
    'menu-items'
  );

  getMenuItems(): Observable<MenuItem[]> {
    return collectionData(this.menuCollection, { idField: 'id' }) as Observable<
      MenuItem[]
    >;
  }

  getMenuItem(id: string): Observable<MenuItem | undefined> {
    const menuDocRef = doc(this.firestore, `menu-items/${id}`);
    return docData(menuDocRef, { idField: 'id' }) as Observable<
      MenuItem | undefined
    >;
  }

  addMenuItem(menu: Omit<MenuItem, 'id'>): Promise<void> {
    return addDoc(this.menuCollection, menu)
      .then((docRef: DocumentReference) => {
        console.log('Menu item added with ID:', docRef.id);
      })
      .catch((error) => {
        console.error('Error adding menu item:', error);
      });
  }

  updateMenuItem(updatedMenuItem: MenuItem): Promise<void> {
    const menuDocRef = doc(this.firestore, `menu-items/${updatedMenuItem.id}`);
    return updateDoc(menuDocRef, {
      title: updatedMenuItem.title,
      description: updatedMenuItem.description,
      price: updatedMenuItem.price,
    }).then(() => {
      console.log('Menu item updated:', updatedMenuItem);
    });
  }

  deleteMenuItem(id: string): Promise<void> {
    const menuDocRef = doc(this.firestore, `menu-items/${id}`);
    return deleteDoc(menuDocRef).then(() => {
      console.log('Menu item deleted:', id);
    });
  }
}
