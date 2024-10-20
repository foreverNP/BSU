import { v4 as uuidv4 } from 'uuid';
import { MenuItem } from './models/menu.model';

export const Menus: MenuItem[] = [
  {
    id: uuidv4(),
    title: 'Пирог',
    price: 5.0,
    description: 'Вкусный яблочный пирог',
  },
  {
    id: uuidv4(),
    title: 'Салат',
    price: 7.5,
    description: 'Свежий овощной салат',
  },
  {
    id: uuidv4(),
    title: 'Бургер',
    price: 10.0,
    description: 'Сочный говяжий бургер',
  },
  {
    id: uuidv4(),
    title: 'Пицца',
    price: 12.0,
    description: 'Пицца с сыром и пепперони',
  },
  {
    id: uuidv4(),
    title: 'Суп',
    price: 6.5,
    description: 'Куриный суп с лапшой',
  },
  {
    id: uuidv4(),
    title: 'Стейк',
    price: 15.0,
    description: 'Сочный стейк из говядины',
  },
  {
    id: uuidv4(),
    title: 'Роллы',
    price: 9.0,
    description: 'Классические роллы с лососем',
  },
  {
    id: uuidv4(),
    title: 'Паста',
    price: 8.0,
    description: 'Паста с томатным соусом и базиликом',
  },
  {
    id: uuidv4(),
    title: 'Чизкейк',
    price: 5.5,
    description: 'Нежный чизкейк с ягодами',
  },
  {
    id: uuidv4(),
    title: 'Кофе',
    price: 3.0,
    description: 'Ароматный черный кофе',
  },
];
