import requests
import os

os.chdir(os.path.dirname(os.path.abspath(__file__)))

headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:121.0) Gecko/20100101 Firefox/121.0',
    'Accept': '*/*',
    'Accept-Language': 'en-US,en;q=0.5',
    # 'Accept-Encoding': 'gzip, deflate, br',
    'Referer': 'https://www.zumiez.com/',
    'Content-Type': 'application/json',
    'Origin': 'https://www.zumiez.com',
    'DNT': '1',
    'Sec-GPC': '1',
    'Connection': 'keep-alive',
    'Sec-Fetch-Dest': 'empty',
    'Sec-Fetch-Mode': 'cors',
    'Sec-Fetch-Site': 'same-site',
    # Requests doesn't support trailers
    # 'TE': 'trailers',
}

json_data = {
    'data': {
        'context': {
            'page': {
                'uri': '/mens.html',
                'locale_country': 'us',
                'locale_language': 'en',
            },
            'user': {
                'uuid': '',
            },
            'store': {},
        },
        'n_item': 100,
        'page_number': 1,
        'sort': {
            'choices': True,
        },
        'content': {
            'product': {
                'field': {
                    'value': [
                        'id',
                        'sku',
                        'brand',
                        'name',
                        'price',
                        'final_price',
                        'image_url',
                        'product_url',
                        'product_group',
                        'product_group_id',
                        'stock_tier',
                        'promotion_flag',
                        'promo_text',
                    ],
                },
            },
        },
        'widget': {
            'rfkid': 'rfkid_10',
        },
        'facet': {
            'max': -1,
            'brand': {
                'total': True,
                'max': -1,
            },
            'color': {
                'total': True,
                'max': -1,
            },
            'promo_text': {
                'total': True,
                'max': -1,
            },
            'final_price': {
                'total': True,
                'max': -1,
            },
            'size': {
                'total': True,
                'max': -1,
            },
            'promotion_flag': {
                'total': True,
                'max': -1,
            },
            'en': {
                'total': True,
                'max': -1,
            },
            'category_tree': {
                'total': True,
                'min_count': 3,
                'max': 100,
                'depth': 10,
                'start_level': 1,
            },
        },
        'rfk_force_exp_features': [
            'useStdCategories',
        ],
    },
}

response = requests.post('https://zumiez-us-prod.sc.zumiez.com/api/search-rec/3', headers=headers, json=json_data)
data = response.json()
total_pages = data.get('total_page')
items = []

for x in range(1,total_pages+1):
	json_data['data']['page_number']=x
	response = requests.post('https://zumiez-us-prod.sc.zumiez.com/api/search-rec/3', headers=headers, json=json_data)
	data = response.json()
	items.append(data.get('content').get('product').get('value'))