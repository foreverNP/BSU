var FormA = [
    { label: 'Разработчики:', elemtype: 'text', name: 'developers', width: 200 },
    { label: 'Разрешить отзывы:', elemtype: 'checkbox', name: 'enablecomments' },
    { label: 'Email:', elemtype: 'email', name: 'email', width: 200 },
    { label: 'Пароль:', elemtype: 'password', name: 'password', width: 200 },
    { label: 'Номер телефона:', elemtype: 'tel', name: 'phone', width: 200 },
    { label: 'Отослать:', elemtype: 'button', value: 'Send' }
];


function createFormElements(formName, formArray) {
    var form = document.getElementById(formName);

    formArray.forEach(function (item) {
        var label = document.createElement('label');
        label.textContent = item.label;
        form.appendChild(label);

        var input = document.createElement('input');
        input.type = item.elemtype;
        input.name = item.name;
        if (item.width) input.style.width = item.width + 'px';
        if (item.value) input.value = item.value;
        form.appendChild(input);

        var error = document.createElement('div');
        error.className = 'error';
        error.style.color = 'red';
        form.appendChild(error);

        input.addEventListener('input', function () {
            validate(input, error);
        });

        if (item.value === 'Send') {
            input.addEventListener('click', function (event) {
                event.preventDefault();

                var isValid = true;
                formArray.forEach(function (item, index) {
                    var input = form.elements[index];
                    var error = form.getElementsByClassName('error')[index];
                    if (!validate(input, error)) {
                        isValid = false;
                    }
                });

                if (isValid) {
                    form.submit();
                }
            });
        }
    });
}

function validate(input, error) {
    var isValid = true;
    var errorMessage = '';

    if (input.type === 'text' && input.value.trim() === '') {
        isValid = false;
        errorMessage = 'This field is required.';
    }

    if (input.type === 'email') {
        var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(input.value)) {
            isValid = false;
            errorMessage = 'Please enter a valid email.';
        }
    }

    if (input.type === 'password' && input.value.length < 8) {
        isValid = false;
        errorMessage = 'Password must be at least 8 characters.';
    }

    if (input.type === 'tel') {
        var phoneRegex = /^\d{9}$/;
        if (!phoneRegex.test(input.value)) {
            isValid = false;
            errorMessage = 'Please enter a valid phone number of 9 digits.';
        }
    }

    error.textContent = errorMessage;

    return isValid;
}

createFormElements('form', FormA);