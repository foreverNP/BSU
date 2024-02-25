var menuArray = [
    {
        name: 'Танцевальный коллектив',
        submenu: [
            {
                name: 'О нас',
                submenu: [
                    { name: 'История', url: '#' },
                    { name: 'Достижения', url: '#' },
                    {
                        name: 'Наши преподаватели',
                        submenu: [
                            { name: 'Иван Иванов', url: '#' },
                            { name: 'Мария Петрова', url: '#' },
                            { name: 'Александр Сидоров', url: '#' },
                        ]
                    },
                ]
            },
            {
                name: 'Наши танцы',
                submenu: [
                    { name: 'Хип-хоп', url: '#' },
                    { name: 'Брейкданс', url: '#' },
                    { name: 'Джаз-модерн', url: '#' },
                    {
                        name: 'Современные танцы',
                        submenu: [
                            { name: 'Контемпорари', url: '#' },
                            { name: 'Лирический джаз', url: '#' },
                        ]
                    },
                ]
            },
            {
                name: 'Контакты',
                url: '#',
            },
        ]
    },
    {
        name: 'Школа танцев',
        submenu: [
            {
                name: 'Расписание занятий',
                url: '#'
            },
            {
                name: 'Преподаватели',
                submenu: [
                    { name: 'Иван Иванов', url: '#' },
                    { name: 'Мария Петрова', url: '#' },
                    { name: 'Александр Сидоров', url: '#' },
                ]
            },
            {
                name: 'Цены',
                url: '#'
            },
            {
                name: 'Наши ученики',
                submenu: [
                    {
                        name: 'Детские группы',
                        submenu: [
                            { name: 'Младшая группа', url: '#' },
                            { name: 'Средняя группа', url: '#' },
                            { name: 'Старшая группа', url: '#' },
                        ]
                    },
                    {
                        name: 'Взрослые группы',
                        submenu: [
                            { name: 'Начальный уровень', url: '#' },
                            { name: 'Средний уровень', url: '#' },
                            { name: 'Продвинутый уровень', url: '#' },
                        ]
                    },
                ]
            },
        ]
    },
    {
        name: 'Новости',
        url: '#'
    },
    {
        name: 'Галерея',
        submenu: [
            {
                name: 'Фотографии',
                url: '#'
            },
            {
                name: 'Видео',
                url: '#'
            },

        ]
    },
];

function showMenu(menuItemsA, parentElem) {
    const menu = document.createElement('div');
    menu.classList.add('menu-container');

    menuItemsA.forEach(item => {
        const menuItem = document.createElement('div');
        const menuLink = document.createElement('a');

        menuLink.textContent = item.name;
        if (item.url) {
            menuLink.href = item.url;
        }

        menuItem.appendChild(menuLink);
        if (item.submenu) {
            let submenuVisible = false;

            menuLink.addEventListener('click', function (event) {
                event.preventDefault();
                const submenu = menuItem.querySelector('.menu-container');

                if (submenuVisible) {
                    submenu.remove();
                    submenuVisible = false;
                } else {
                    showMenu(item.submenu, menuItem);
                    submenuVisible = true;
                }
            });
        }

        menu.appendChild(menuItem);
    });

    parentElem.appendChild(menu);
}

const menu = document.getElementById('menu');
showMenu(menuArray, menu);