let mainPage, secondPage;

mainPage = {
    pagename: "main",
    header: 'Главная страница',
    menu: [
        {
            text: 'Перейти на другую страницу', url: '#table'
        },
    ],
    mainContent: {
        title: 'Танцевальный коллектив',
        image: './img/dance.jpg',
        text: 'Добро пожаловать в мир танцевального коллектива! ' +
            'Здесь, наш талантливый ансамбль танцоров привносит в жизнь волнующие и захватывающие выступления, полные страсти и харизмы.<br> ' +
            '<br> Наша история началась с момента, когда несколько преданных танцоров объединили свои усилия, чтобы создать нечто уникальное. ' +
            'С течением времени наш коллектив стал символом креативности и совершенства в мире танцев.<br> ' +
            '<br> В нашем репертуаре вы найдете удивительные постановки, отражающие разнообразие стилей и культур.' +
            ' Мы сочетаем в себе классические танцевальные техники с современными тенденциями, создавая потрясающие и неповторимые выступления. ' +
            'Наши танцы переносят зрителей в мир эмоций и вдохновения.<br>' +
            ' <br> В процессе творчества мы находим вдохновение в различных культурах, музыкальных направлениях и современных тенденциях.' +
            ' Это позволяет нам создавать уникальные номера, которые заставляют зрителей переживать невероятные моменты вместе с нами.<br>' +
            ' <br> Верим, что искусство танца способно преобразить мир, и танцевальный коллектив является воплощением этой веры. ' +
            'Мы приглашаем вас присоединиться к нашему увлекательному путешествию по миру танцев и насладиться неповторимой энергией, которую приносит каждое наше выступление.<br>',

    },
    footer: {
        text: 'Танцуй и побеждай<br>Контактный телефон: 123-456-789<br>' +
            'Адрес: ул. Танцевальная, 1<br>Copyright © 2024 Lev Inc. All rights reserved.',
    },
};

secondPage = {
    pagename: "table",
    header: 'Вторая страница',
    menu: [
        {
            text: 'Перейти на главную страницу', url: '#main'
        },
    ],
    mainContent: {
        table: null
    },
    footer: {
        text: 'Танцуй и побеждай<br>Контактный телефон: 123-456-789<br>' +
            'Адрес: ул. Танцевальная, 1<br>Copyright © 2024 Lev Inc. All rights reserved.',
    },
};

function renderContent(page, mainParentId) {
    const app = document.getElementById(mainParentId);
    app.innerHTML = '';
    //////////////////////////////////////////////////////////////
    const header = document.createElement('div');
    header.className = 'header-style';
    header.textContent = page.header;
    app.appendChild(header);
    //////////////////////////////////////////////////////////////
    const menu = document.createElement('div');
    menu.className = 'menu-style';

    const menuList = document.createElement('ul');

    page.menu.forEach(item => {
        const menuItem = document.createElement('li');
        const menuButton = document.createElement('button');
        menuButton.textContent = item.text;
        menuButton.onclick = function () {
            location.hash = item.url;
        };
        menuItem.appendChild(menuButton);
        menuList.appendChild(menuItem);
    });

    menu.appendChild(menuList);
    app.appendChild(menu);
    //////////////////////////////////////////////////////////////
    const mainContent = document.createElement('div');
    mainContent.className = 'maincontent-style';

    if (page.mainContent.title) {
        const mainTitle = document.createElement('h2');
        mainTitle.style.textAlign = 'center';
        mainTitle.textContent = page.mainContent.title;
        mainContent.appendChild(mainTitle);
    }

    if (page.mainContent.image) {
        const mainImage = document.createElement('img');
        mainImage.src = page.mainContent.image;
        mainImage.className = 'maincontent-image';
        mainContent.appendChild(mainImage);
    }

    if (page.mainContent.text) {
        const mainText = document.createElement('p');
        mainText.innerHTML = page.mainContent.text;
        mainContent.appendChild(mainText);
    }

    if (page.mainContent.table) {
        const mainTable = document.createElement('div');
        mainTable.className = 'maincontent-table-style';
        mainTable.appendChild(page.mainContent.table);
        mainContent.appendChild(mainTable);
    }

    app.appendChild(mainContent);
    //////////////////////////////////////////////////////////////
    const footer = document.createElement('div');
    footer.className = 'footer-style';
    footer.innerHTML = page.footer.text;
    app.appendChild(footer);
}

function LoadDances() {
    let result;

    $.ajax("../data/dances.json", {
        type: 'GET',
        dataType: 'json',
        success: function (data) {
            result = DataLoaded(data);
        },
        error: ErrorHandler,
        async: false
    });

    return result;
}

function DataLoaded(data) {
    console.log('загруженные через AJAX данные:');
    console.log(typeof data, data);

    const table = document.createElement('table');
    table.className = 'maincontent-table';

    const thead = document.createElement('thead');
    const headerRow = document.createElement('tr');
    const nameTh = document.createElement('th');
    nameTh.textContent = 'Название танца';
    const descriptionTh = document.createElement('th');
    descriptionTh.textContent = 'Описание';
    headerRow.appendChild(nameTh);
    headerRow.appendChild(descriptionTh);
    thead.appendChild(headerRow);
    table.appendChild(thead);

    const tbody = document.createElement('tbody');
    for (const [name, description] of Object.entries(data)) {
        const row = document.createElement('tr');
        const nameTd = document.createElement('td');
        nameTd.textContent = name;
        const descriptionTd = document.createElement('td');
        descriptionTd.textContent = description;
        row.appendChild(nameTd);
        row.appendChild(descriptionTd);
        tbody.appendChild(row);
    }
    table.appendChild(tbody);

    return table;
}

function ErrorHandler(jqXHR, StatusStr, ErrorStr) {
    alert(StatusStr + ' ' + ErrorStr);
}

function switchToStateFromURLHash() {
    var URLHash = window.location.hash;

    switch (URLHash) {
        case '#main':
            renderContent(mainPage, 'app');
            break;
        case '#table':
            secondPage.mainContent.table = LoadDances();
            renderContent(secondPage, 'app');
            break;
        case '':
            renderContent(mainPage, 'app');
            break;
    }
}

window.onhashchange = switchToStateFromURLHash;

switchToStateFromURLHash();