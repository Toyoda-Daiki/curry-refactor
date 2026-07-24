'use strict';
$(function () {
	calc_price();
	$('.size').on('change', function () {
		calc_price();
	});

	$('.checkbox').on('change', function () {
		calc_price();
		update_topping_images();
	});

	$('#currynum').on('change', function () {
		calc_price();
	});

	// 各トッピングIDのドラッグ後の位置を保存するオブジェクト
	// { toppingId: { left: 'px値', top: 'px値' } }
	let savedPositions = {};

	// 値段の計算をして変更する関数
	function calc_price() {
		let size = $('.size:checked').val();
		let topping_count = $('#topping input:checkbox:checked').length;
		let curry_num = $('#currynum option:selected').val();
		let size_price = 0;
		let topping_price = 0;
		if (size == 'M') {
			size_price = Number($('#sizeM').val())
			topping_price = 200 * topping_count;
		} else {
			size_price = Number($('#sizeL').val())
			topping_price = 300 * topping_count;
		}
		let price = (size_price + topping_price) * curry_num;
		$('#totalprice').text(price.toLocaleString());
	}

	function update_topping_images() {
		$('#topping-overlay-container').empty();
		let selectedToppingsCount = 0;

		$('#topping input:checkbox:checked').each(function () {
			let toppingId = $(this).data('topping-id');

			// 位置を動的に計算（最大28種類対応できるよう、縦横に分散させる）
			let topPos = 15 + ((selectedToppingsCount * 17) % 45); // 15% 〜 60%
			let leftPos = 10 + ((selectedToppingsCount * 23) % 45); // 10% 〜 55%

			let img = $('<img>').attr('src', '/img_curry/toppings/topping_' + toppingId + '.png');

			// 画像が存在しない場合は、リンク切れアイコンが表示されないように要素を削除する
			img.on('error', function () {
				$(this).remove();
			});

			// topping_14とtopping_22はカレールーを覆う大きさで表示
			// リファクタリング課題#34 ==を===に変更し型強制によるリスクを排除
			let isLargeTopping = (toppingId === 14 || toppingId === 22);
			let toppingWidth = isLargeTopping ? '75%' : '30%';

			// savedPositionsに保存済みの位置があればそれを使う、なければデフォルト位置
			let hasSaved = savedPositions[toppingId] != null;
			let toppingTop  = hasSaved ? savedPositions[toppingId].top  : (isLargeTopping ? '20%' : topPos + '%');
			let toppingLeft = hasSaved ? savedPositions[toppingId].left : (isLargeTopping ? '10%' : leftPos + '%');

			img.css({
				'position': 'absolute',
				'top': toppingTop,
				'left': toppingLeft,
				'width': toppingWidth,
				'height': 'auto',
				'object-fit': 'contain',
				'filter': 'drop-shadow(2px 4px 6px rgba(0,0,0,0.5))',
				'z-index': selectedToppingsCount,
				'cursor': 'grab',
				'user-select': 'none',
				'pointer-events': 'auto'
			});

			// ドラッグで移動できる機能を追加（位置を savedPositions に保存）
			make_draggable(img, toppingId);

			$('#topping-overlay-container').append(img);
			selectedToppingsCount++;
		});

		// チェックが外れたトッピングの保存位置をクリア
		let checkedIds = [];
		$('#topping input:checkbox:checked').each(function () {
			checkedIds.push($(this).data('topping-id'));
		});
		Object.keys(savedPositions).forEach(function (id) {
			if (!checkedIds.includes(Number(id))) {
				delete savedPositions[id];
			}
		});
	}

	// トッピング画像をドラッグで移動できるようにする
	function make_draggable(img, toppingId) {
		let isDragging = false;
		let startX, startY, startLeft, startTop;

		img.on('mousedown', function (e) {
			e.preventDefault();
			isDragging = true;
			startX = e.clientX;
			startY = e.clientY;

			let container = $('#topping-overlay-container');
			// コンテナ内でのpx座標を取得
			startLeft = img[0].offsetLeft;
			startTop = img[0].offsetTop;

			img.css('cursor', 'grabbing');
			// ドラッグ中は最前面に表示
			img.css('z-index', 9999);

			$(document).on('mousemove.drag', function (e) {
				if (!isDragging) return;

				let dx = e.clientX - startX;
				let dy = e.clientY - startY;

				let containerW = container.width();
				let containerH = container.height();
				let imgW = img.width();
				let imgH = img.height();

				// コンテナ内に収まるよう制限
				let newLeft = Math.max(0, Math.min(startLeft + dx, containerW - imgW));
				let newTop = Math.max(0, Math.min(startTop + dy, containerH - imgH));

				img.css({
					'left': newLeft + 'px',
					'top': newTop + 'px'
				});
			});

			$(document).on('mouseup.drag', function () {
				if (!isDragging) return;
				isDragging = false;
				img.css('cursor', 'grab');
				// ドラッグ後の位置を保存
				savedPositions[toppingId] = {
					left: img[0].offsetLeft + 'px',
					top: img[0].offsetTop + 'px'
				};
				$(document).off('mousemove.drag mouseup.drag');
			});
		});
	}
});
